package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundActivityService;
import playground.logic.Services.PlaygroundElementService;
import playground.logic.Services.PlaygroundUserService;
import playground.plugins.ReadFromBoardResult;
import playground.plugins.Message;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestsActivity {
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

	private ObjectMapper jackson = new ObjectMapper();

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

	@After
	public void teardown() {
		// cleanup database
		testHelper.teardown();
		this.activityService.cleanup();
		this.elementService.cleanup();
	}

	@Test(expected = Exception.class)
	public void testActivateElementWithInvalidActivityType() throws Exception {

		// Given Server is up
		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		// create Manger to add element
		String userEmail = "manager@email.com";
		String userPlayground = "2019a.talin";
		testHelper.addNewUser(userEmail, "Manager", true);

		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity elementEntity = this.elementService.addNewElement(userPlayground, userEmail, new ElementEntity());

		// create Player to post Activity
		userEmail = "player@email.com";
		userPlayground = "2019a.talin";
		testHelper.addNewUser(userEmail, "Player", true);

		// When I POST activity with
		String elementId = elementEntity.getId();
		String elementPlayground = playground;
		String type = "Play";

		// check that element exists
		this.elementService.getElement(userPlayground, userEmail, elementId, elementPlayground);

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class, playground, userEmail);

		// Then the response status is <> 2xx
	}

	@Test(expected = Exception.class)
	public void testActivatingNotExistingElement() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		// create Player to post Activity
		String userEmail = "player2@email.com";
		String userPlayground = "2019a.talin";
		testHelper.addNewUser(userEmail, "Player", true);

		// When I POST activity with
		String elementId = "0";
		String elementPlayground = playground;
		String type = "Feed";

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class, playground, userEmail);

		// Then the response status is <> 2xx
	}

	// A
	@Test
	public void testFeedActivateElementWithTypeAnimalSuccessfully() throws Exception {
		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity Animal = new ElementEntity();
		Animal.setType("Animal");
		Long numberOfPointsToAdd = new Long(1);

		// Given Server is up
		// database contains an manager to add element
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains element

		ElementEntity element = elementService.addNewElement(playground, managerEmail, Animal);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);
		UserEntity user = userService.getUser(playground, userEmail);
		Long OldNumberOfPoints = user.getPoints();

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(Animal.getId());
		newActivityTO.setElementPlayground(Animal.getPlayground());
		newActivityTO.setType("Feed");
		Object rv = this.restTemplate.postForObject(url, newActivityTO, Message.class, playground, userEmail);

		// Then the response status is 2xx and
		// And the database contains user with Points + 1

		user = userService.getUser(playground, userEmail);
		Long NewNumberOfPoints = user.getPoints();
		Map<String, Object> rvMap = this.jackson.readValue(this.jackson.writeValueAsString(rv), Map.class);

		assertThat(OldNumberOfPoints + numberOfPointsToAdd).isEqualTo(NewNumberOfPoints);

		// and body is:
		
		
		assertThat(rvMap.get("message")).isEqualTo(numberOfPointsToAdd+" point to" + user.getUsername() + " for feed " + Animal.getName());

		// and the database contains activity:
		String activity_id = rvMap.get("id") + "";
		ActivityEntity Activity = this.activityService.getActivity(playground, userEmail, activity_id, playground);

		assertThat(Activity).isNotNull().extracting("playground", "id", "elementPlayground", "elementId", "type")
				.containsExactly(playground, activity_id, playground, element.getId(), "Feed");
	}

	// A
	@Test(expected = Exception.class)
	public void testFeedActivateElementWithTypeBoard() throws Exception {
		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity Board = new ElementEntity();
		Board.setType("Board");

		// Given Server is up
		// database contains an manager to add element
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains element with type Board
		elementService.addNewElement(playground, managerEmail, Board);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);
		UserEntity user = userService.getUser(playground, userEmail);

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(Board.getId());
		newActivityTO.setElementPlayground(Board.getPlayground());
		newActivityTO.setType("Feed");

		this.restTemplate.postForObject(url, newActivityTO, Message.class, playground, userEmail);

		// Then the response status <> 2xx
	}

	// S
	@Test
	public void testPostMessageActivateElementWithTypeBoardSuccessfully() throws Exception {
		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity board = new ElementEntity();
		board.setName("Lost Animals Board");
		board.setType("Board");

		// Given Server is up
		// database contains an manager to add element
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains Board element

		ElementEntity element = elementService.addNewElement(playground, managerEmail, board);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);
		UserEntity user = userService.getUser(playground, userEmail);

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(board.getId());
		newActivityTO.setElementPlayground(board.getPlayground());
		newActivityTO.setType("PostMessage");
		Object rv = this.restTemplate.postForObject(url, newActivityTO, Message.class, playground, userEmail);

		// Then the response status is 2xx

		Map<String, Object> rvMap = this.jackson.readValue(this.jackson.writeValueAsString(rv), Map.class);

		// and body is:
		assertThat(rvMap.get("message"))
				.isEqualTo("the user " + user.getUsername() + " posted a message in " + board.getName());

		// and the database contains activity:
		String activity_id = rvMap.get("id") + "";
		ActivityEntity Activity = this.activityService.getActivity(playground, userEmail, activity_id, playground);

		assertThat(Activity).isNotNull().extracting("playground", "id", "elementPlayground", "elementId", "type")
				.containsExactly(playground, activity_id, playground, element.getId(), "PostMessage");

	}

	// S
	@Test(expected = Exception.class)
	public void testPostMessageActivateElementWithTypeAnimal() throws Exception {
		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity animal = new ElementEntity();
		animal.setType("Animal");

		// Given Server is up
		// database contains an manager to add element
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains element with type Board
		elementService.addNewElement(playground, managerEmail, animal);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);
		userService.getUser(playground, userEmail);

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(animal.getId());
		newActivityTO.setElementPlayground(animal.getPlayground());
		newActivityTO.setType("PostMessage");

		this.restTemplate.postForObject(url, newActivityTO, Message.class, playground, userEmail);

	}

	// I
	@Test
	public void testPetActivateElementWithTypeAnimalSuccessfully() throws Exception {
		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity animal = new ElementEntity();
		animal.setType("Animal");
		Long numberOfPointsToAdd = new Long(10);

		// Given Server is up
		// database contains an manager to add element
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains element

		ElementEntity element = elementService.addNewElement(playground, managerEmail, animal);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);
		UserEntity user = userService.getUser(playground, userEmail);
		Long OldNumberOfPoints = user.getPoints();

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(animal.getId());
		newActivityTO.setElementPlayground(animal.getPlayground());
		newActivityTO.setType("Pet");
		Object rv = this.restTemplate.postForObject(url, newActivityTO, Message.class, playground, userEmail);

		// Then the response status is 2xx and
		// And the database contains user with Points + 10

		user = userService.getUser(playground, userEmail);
		Long NewNumberOfPoints = user.getPoints();
		Map<String, Object> rvMap = this.jackson.readValue(this.jackson.writeValueAsString(rv), Map.class);

		assertThat(OldNumberOfPoints + numberOfPointsToAdd).isEqualTo(NewNumberOfPoints);

		// and body is:
		assertThat(rvMap.get("message")).isEqualTo(numberOfPointsToAdd+ " point to" + user.getUsername() + " for pet " + animal.getName());
		// and the database contains activity:
		String activity_id = rvMap.get("id") + "";
		ActivityEntity Activity = this.activityService.getActivity(playground, userEmail, activity_id, playground);

		assertThat(Activity).isNotNull().extracting("playground", "id", "elementPlayground", "elementId", "type")
				.containsExactly(playground, activity_id, playground, element.getId(), "Pet");
	}

	// I
	@Test(expected = Exception.class)
	public void testPetActivateElementWithTypeBoard() throws Exception {
		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity Board = new ElementEntity();
		Board.setType("Board");

		// Given Server is up
		// database contains an manager to add element
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains element with type Board
		elementService.addNewElement(playground, managerEmail, Board);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(Board.getId());
		newActivityTO.setElementPlayground(Board.getPlayground());
		newActivityTO.setType("Pet");

		this.restTemplate.postForObject(url, newActivityTO, Message.class, playground, userEmail);

		// Then the response status <> 2xx
	}

	// T
	@Test
	public void testReadFromBoardWithDefaultPaginationSuccessfully() throws Exception {
		int defaultSize = 10;

		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity Board = new ElementEntity();
		Board.setType("Board");

		// Given Server is up
		// database contains a manager
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains element with type Board
		ElementEntity element = elementService.addNewElement(playground, managerEmail, Board);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		// And the database contains 10 messages posted to this board
		for (int i = 0; i < defaultSize; i++) {
			ActivityTO newActivityTO = new ActivityTO();
			newActivityTO.setElementId(Board.getId());
			newActivityTO.setElementPlayground(Board.getPlayground());
			newActivityTO.setType("PostMessage");
			this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class, playground, userEmail);
		}

		// When I Post Activity to read all messages of board x
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(Board.getId());
		newActivityTO.setElementPlayground(Board.getPlayground());
		newActivityTO.setType("ReadFromBoard");

		// newActivityTO.getAttributes().put("page", 1);
		// newActivityTO.getAttributes().put("size", 2);

		List<String> readmessages = null;

		try {
			ReadFromBoardResult rv = this.restTemplate.postForObject(url, newActivityTO, ReadFromBoardResult.class,
					playground, userEmail);
			readmessages = (List<String>) rv.getResults();

			// Then the response status is 2xx and
			// body contains 10 messages
			assertThat(readmessages).hasSize(defaultSize);

			// and the database contains activity:
			String activity_id = rv.getActivity_id();
			ActivityEntity Activity = this.activityService.getActivity(playground, userEmail, activity_id, playground);

			assertThat(Activity).isNotNull().extracting("playground", "id", "elementPlayground", "elementId", "type")
					.containsExactly(playground, activity_id, playground, element.getId(), "ReadFromBoard");

		} catch (HttpServerErrorException e) {
			System.err.println(e.getResponseBodyAsString());
			throw e;
		}

	}

	// T
	@Test
	public void testReadFromBoardUsingPaginationSuccessfully() throws Exception {
		int defaultSize = 10;

		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity Board = new ElementEntity();
		Board.setType("Board");

		// Given Server is up
		// database contains a manager
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains element with type Board
		ElementEntity element = elementService.addNewElement(playground, managerEmail, Board);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		// And the database contains 10 messages posted to this board
		for (int i = 0; i < defaultSize; i++) {
			ActivityTO newActivityTO = new ActivityTO();
			newActivityTO.setElementId(Board.getId());
			newActivityTO.setElementPlayground(Board.getPlayground());
			newActivityTO.setType("PostMessage");
			this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class, playground, userEmail);
		}

		// When I Post Activity to read all messages of board x
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(Board.getId());
		newActivityTO.setElementPlayground(Board.getPlayground());
		newActivityTO.setType("ReadFromBoard");

		newActivityTO.getAttributes().put("page", 2);
		newActivityTO.getAttributes().put("size", 4);

		List<String> readmessages = null;

		try {
			ReadFromBoardResult rv = this.restTemplate.postForObject(url, newActivityTO, ReadFromBoardResult.class,
					playground, userEmail);
			readmessages = (List<String>) rv.getResults();

			// Then the response status is 2xx and
			// body contains 10 messages
			assertThat(readmessages).hasSize(2);

			// and the database contains activity:
			String activity_id = rv.getActivity_id();
			ActivityEntity Activity = this.activityService.getActivity(playground, userEmail, activity_id, playground);

			assertThat(Activity).isNotNull().extracting("playground", "id", "elementPlayground", "elementId", "type")
					.containsExactly(playground, activity_id, playground, element.getId(), "ReadFromBoard");

		} catch (HttpServerErrorException e) {
			System.err.println(e.getResponseBodyAsString());
			throw e;
		}

	}

	// T
	@Test(expected = Exception.class)
	public void testReadFromBoardWithInvalidElementType() throws Exception {
		// create Manger to add element
		String managerEmail = "manager@mail.com";
		String userEmail = " user@email.com";
		ElementEntity animal = new ElementEntity();
		animal.setType("Animal");

		// Given Server is up
		// database contains a manager
		testHelper.addNewUser(managerEmail, "Manager", true);

		// And the database contains element with type Animal
		ElementEntity element = elementService.addNewElement(playground, managerEmail, animal);

		// And the database contains player
		testHelper.addNewUser(userEmail, "Player", true);

		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		// When I Post Activity to read all messages from this element
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(element.getId());
		newActivityTO.setElementPlayground(element.getPlayground());
		newActivityTO.setType("ReadFromBoard");

		ReadFromBoardResult rv = this.restTemplate.postForObject(url, newActivityTO, ReadFromBoardResult.class,
				playground, userEmail);
		// Then the response status is <> 2xx
	}
}
