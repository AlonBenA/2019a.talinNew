package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import playground.logic.Location;
import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundActivityService;
import playground.logic.Services.PlaygroundElementService;
import playground.logic.Services.PlaygroundUserService;
import playground.plugins.Message;
import playground.plugins.ReadFromBoardResult;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties={"spring.profiles.active=default"})
public class WebUIComplexTest {
	@Autowired
	private PlaygroundActivityService activityService;

	@Autowired
	private PlaygroundElementService elementService;

	@Autowired
	private PlaygroundUserService userService;

	@Autowired
	private TestHelper testHelper;

	private RestTemplate restTemplate;

	private String playground;

	@Value("${playground}") // set playground as "2019a.talin"
	private void setPlayground(String playground) {
		this.playground = playground;
	}

	@LocalServerPort
	private int port;

	private String base_url;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		base_url = "http://localhost:" + port;
	}

	@Before
	public void setup() {
		
	}

	@Test
	public void complexTest() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/users";
		String avatar = "https://goo.gl/images/WqDt96";
		
		// and the database contains manager and two players
		UserTO manager1 = userSignupValidatenAndLogin(url, "manager1@usermail.com",
				playground, "manager1", "Manager", avatar);
		UserTO player1 = userSignupValidatenAndLogin(url, "player1@usermail.com",
				playground, "player1", "Player", avatar);
		UserTO player2 = userSignupValidatenAndLogin(url, "player2@usermail.com",
				playground, "player2", "Player", avatar);
		
		// manager1 update his username
		// When I Put http://localhost:8083/playground/users/userPlayground/userEmail
		String username = "GreatManager";
		manager1.setUsername(username);
		url = base_url + "/playground/users/{playground}/{email}";
		try {
			this.restTemplate.put(url, manager1, playground, manager1.getEmail());
			UserEntity updatedUser = userService.getUser(playground, "manager1@usermail.com");
			assertThat(updatedUser.getUsername()).contains(username);
		}catch (HttpClientErrorException e) {
			System.err.println(e.getResponseBodyAsString());
			throw e;
		}
		
		int numberOfAllElements = 12; 
		
		// manager1 creates 10 elements with type animal and location(x,y)
		//that grows by one for each animal, and 5 of them with name = “cat”,
		//and 2 of the cats with expiration date = current time.
		// manager1 creates board1 and board2
		createElementsForTest(manager1.getPlayground(), manager1.getEmail());
		
		// manager wants to see all the elements using pagination size = 100
		 ElementTO[] allElements = getAllElements(manager1.getUsername(), manager1.getPlayground(), manager1.getEmail(), numberOfAllElements);
		
		int distance = 5;
		int x = 0;
		int y = 0;
		//manager wants to see all near elements at distance 5
		getAllNearElements(manager1.getUsername(),manager1.getPlayground(), manager1.getEmail(), x, y, distance, 5);
		
		//manager wants to see all the cats using default pagination
		url = base_url + "/playground/elements/{userPlayground}/{email}/search/name/cat";
		// when I Get with url
		ElementTO[] cats = this.restTemplate.getForObject(url, ElementTO[].class, manager1.getPlayground(), manager1.getEmail());
		// then
		assertThat(cats).isNotNull().hasSize(5);
		System.err.println(manager1.getUsername() + " found " + cats.length + " cats");
		
		//player2 wants to see all the elements in the shelter, and sees only 10
		getAllElements(player2.getUsername(), player2.getPlayground(), player2.getEmail(), 10);
		
		// player2 now wants only to see the near elements, distance = 3, and sees only 2 elements
		distance = 3;
		getAllNearElements(player2.getUsername(), player2.getPlayground(), player2.getEmail(), x, y, distance, 2);
		
		// manager1 falls in love with cat1 and adds him a nickname=”honey”
		url = base_url + "/playground/elements/{userPlayground}/{userEmail}/{Playground}/{Id}";
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("Nickname", "honey");
		// When I Put with url
		ElementTO updatedElementTO = cats[0];
		updatedElementTO.setAttributes(attributes);
		this.restTemplate.put(url, updatedElementTO, manager1.getPlayground(), manager1.getEmail(), updatedElementTO.getPlayground(),
			updatedElementTO.getId());
		
		// manager1 check that cat1 has the new nickname
		System.err.println(manager1.getUsername() + " updated " + updatedElementTO.getName());
		url = base_url + "/playground/elements/{userPlayground}/{email}/{playground}/{id}";
		ElementTO actualElement = this.restTemplate.getForObject(url, ElementTO.class, manager1.getPlayground(), manager1.getEmail(),
				updatedElementTO.getPlayground(), updatedElementTO.getId());
		assertThat(actualElement).isEqualToComparingFieldByField(updatedElementTO);
		System.err.println(actualElement);
		
		// player1 posts 3 messages to board #1
		ElementTO board1 = allElements[0];
		for(int i = 0; i < 3; i++)
			postMessage(board1, player1.getPlayground(), player1.getEmail());
		
		//player1 posts 1 message to Board #0
		ElementTO board0 = allElements[1];
		postMessage(board0, player1.getPlayground(), player1.getEmail());
		
		//player2 reads messages from board #1
		// When I Post Activity to read all messages 
		url = base_url + "/playground/activities/{userPlayground}/{email}";
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(board1.getId());
		newActivityTO.setElementPlayground(board1.getPlayground());
		newActivityTO.setType("ReadFromBoard");
		List<String> readmessages = null;
		ReadFromBoardResult rv = this.restTemplate.postForObject(url, newActivityTO, ReadFromBoardResult.class,
							player2.getPlayground(), player2.getEmail());
		readmessages = (List<String>) rv.getResults();
		// Then player2 sees 3 messages
		System.err.println(player2.getUsername() + "reads from board " + board1.getName());
		assertThat(readmessages).hasSize(3);
		for (String string : readmessages) {
			System.err.println(string);
		}
		
		//player 1 feeds 3 times cat1
		// player1 tries to feed again the cat, but the cat is already full
		String activityType = "Feed";
		for(int i = 0; i < 3; i++)
			activteAnimal(player1.getEmail(), player1.getPlayground(), updatedElementTO, activityType);
		try {
			activteAnimal(player1.getEmail(), player1.getPlayground(), updatedElementTO, activityType);
		}catch (HttpServerErrorException e) {
			System.err.println("player1 tries to feed again the cat1, but the cat is already full");
			System.err.println(e.getResponseBodyAsString());
		}
		//player2 pets 10 times cat2
		//player1 tries to pet cat2, but the cat is tired 
		activityType = "Pet";
		for(int i = 0; i < 10; i++)
			activteAnimal(player2.getEmail(), player2.getPlayground(), cats[1], activityType);
		try {
			activteAnimal(player1.getEmail(), player1.getPlayground(), cats[1], activityType);
		}catch (HttpServerErrorException e) {
			System.err.println("player1 tries to pet again the cat2, but the cat is tired ");
			System.err.println(e.getResponseBodyAsString());
		}
		
		//player1 is so sad that he tries to pet board1 and fails to do so
		try {
			activteAnimal(player1.getEmail(), player1.getPlayground(), board1, activityType);
		}catch (HttpServerErrorException e) {
			System.err.println("player1 is so sad that he tries to pet board1 and fails to do so");
			System.err.println(e.getResponseBodyAsString());
		}
	}

	private void activteAnimal(String email, String playground, ElementTO animal, String activityType) {
		String url = base_url + "/playground/activities/{userPlayground}/{email}";
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(animal.getId());
		newActivityTO.setElementPlayground(animal.getPlayground());
		newActivityTO.setType(activityType);
		this.restTemplate.postForObject(url, newActivityTO, Message.class, playground, email);
	}

	private void postMessage(ElementTO board11, String userPlayground, String userEmail) {
		String url = base_url + "/playground/activities/{userPlayground}/{email}";
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(board11.getId());
		newActivityTO.setElementPlayground(board11.getPlayground());
		newActivityTO.setType("PostMessage");
		try {
			this.restTemplate.postForObject(url, newActivityTO, Message.class, userPlayground, userEmail);
		}catch (HttpServerErrorException e) {
			System.err.println(e.getResponseBodyAsString());
			throw e;
		}
	}

	private void getAllNearElements(String username, String userPlayground, String userEmail, int x, int y, int distance, int expectedNumOfElements) {
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/near/{x}/{y}/{distance}";
		// when
		ElementTO[] allNearElements = this.restTemplate.getForObject(url, ElementTO[].class, userPlayground, userEmail, x, y,
						distance);
		// then the number of elements is 5
		assertThat(allNearElements).isNotNull().hasSize(expectedNumOfElements);
		System.err.println(username + " gets all near elements:");
		for (ElementTO element : allNearElements) {
			System.err.println(element.toString());
		}
	}

	private ElementTO[] getAllElements(String username, String userPlayground, String userEmail, int expectedNumberOfElements) {
		// When I Get with url
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/all?size={size}";
		ElementTO[] allElements = this.restTemplate.getForObject(url, ElementTO[].class, userPlayground, userEmail, 100);
		// then the number of all elements is 12
		assertThat(allElements).isNotNull().hasSize(expectedNumberOfElements);
		System.err.println(username + " gets all the elements:");
		for (ElementTO element : allElements) {
			System.err.println(element.toString());
		}
		return allElements;
	}

	private void createElementsForTest(String managerPlayground, String managerEmail) {
		int numberOFElements = 10;
		String url = base_url + "/playground/elements/{userPlayground}/{email}";
		String name;
		Date expirationDate = null;
		Map<String, Object> attributes = new HashMap<>();
		for(int i = 0; i < numberOFElements; i++){
			if(i % 2 == 0)
				name = "cat";
			else
				name = "animal #" + i;
			if(i % 5 == 0)
				expirationDate = new Date(System.currentTimeMillis());
			else
				expirationDate = null;
			Location location = new Location(i,i);
			ElementTO elementTO = new ElementTO(location, name, expirationDate, "Animal", attributes, null, null);
			ElementTO elementAfterPost = this.restTemplate.postForObject(url, elementTO, ElementTO.class, managerPlayground,
					managerEmail);
			System.err.println("Created: " + elementAfterPost.toString());		
		}
		
		for(int i = 0; i < 2; i++){
			name = "board #" + i;
			Location location = new Location(numberOFElements, numberOFElements);
			ElementTO elementTO = new ElementTO(location, name, expirationDate, "Board", attributes, null, null);
			ElementTO elementAfterPost = this.restTemplate.postForObject(url, elementTO, ElementTO.class, managerPlayground,
					managerEmail);
			System.err.println("Created: " + elementAfterPost.toString());		
		}	
	}

	@After
	public void teardown() {
		// cleanup database
		testHelper.teardown();
		this.activityService.cleanup();
		this.elementService.cleanup();
		this.userService.cleanup();
	}
	
	private UserTO userSignupValidatenAndLogin(String url, String email, String userPlaground, String username, String role, String avatar) {
		UserTO loginUser = null;
		try {
	//		When user wants to sign up
			//POST http://localhost:8083/playground/users with 
			NewUserForm newUserForm = new NewUserForm(email, username, avatar, role);
			UserTO userTO = this.restTemplate.postForObject(url, newUserForm, UserTO.class);
			
			
			// preparation for testing confirmation 
			UserEntity userEntity = userService.getUser(userTO.getPlayground(), userTO.getEmail());
			String code = userEntity.getCode();
			
	//		When user confirm himself
			//GET http://localhost:8083/playground/users/confirm/userPlayground/email/code
			url = base_url + "/playground/users/confirm/{playground}/{email}/{code}";
			this.restTemplate.getForObject(url, UserTO.class, userPlaground, email, code);
			
			//When user login
			//GET http://localhost:8083/playground/users/login/userPlayground/userEmail
			url = base_url + "/playground/users/login/{playground}/{email}";
			loginUser = this.restTemplate.getForObject(url, UserTO.class, userPlaground, email);
		}catch (HttpClientErrorException e) {
			System.err.println(e.getResponseBodyAsString());
		}

		return loginUser;
	}

}
