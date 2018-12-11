/*package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.Location;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.ElementAlreadyExistException;
import playground.logic.Services.PlaygroundActivityService;
import playground.logic.Services.PlaygroundElementService;
import playground.logic.Services.PlaygroundService;
import playground.logic.Services.PlaygroundUserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITests {

	//@Autowired
	//private PlaygroundService playgroundService;
	
	@Autowired
	private PlaygroundElementService elementService;
	
	@Autowired
	private PlaygroundUserService userService;
	
	@Autowired
	private PlaygroundActivityService activityService;

	private RestTemplate restTemplate;

//	private final String PLAYGROUND = "2019a.talin";

	private String playground;
	
	@Value("${playground}")	//set playground as "2019a.talin"
	private void setPlayground(String playground) {
		this.playground = playground;
	}

	@LocalServerPort
	private int port;

	private String base_url;

	private ObjectMapper jackson;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.jackson = new ObjectMapper();
		base_url = "http://localhost:" + port;
	}

	@Before
	public void setup() {

	}

	@After
	public void teardown() {
		// cleanup database
		//this.playgroundService.cleanup();
		this.elementService.cleanup();
		this.userService.cleanup();
		this.activityService.cleanup();
	}

	// S
	@Test
	public void testUserSignupSuccessfully() throws Exception {
		String url = base_url + "/playground/users";
		String email = "usermail2@usermail.com";
		String username = "user2";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Player";

//		Given Server is up

//		When I POST http://localhost:8083/playground/users with 
//		{"email":"usermail2@usermail.com", "username":"user2", 
//				"avatar":"https://goo.gl/images/WqDt96", "role":"Player"}

		NewUserForm newUserForm = this.jackson
				.readValue(
						"{" + "\"email\":\"usermail2@usermail.com\",\"username\":\"user2\","
								+ "\"avatar\":\"https://goo.gl/images/WqDt96\",\"role\":\"Player\"" + "}",
						NewUserForm.class);
		this.restTemplate.postForObject(url, newUserForm, NewUserForm.class);

//		  with headers:
//		  	Accept: application/json
//		 	Content-Type:  application/json	
//		 Then the response status is 2xx and body is 
//		 {"email": "usermail2@usermail.com", "playground": "2019a.Talin", 
//				"username": "user2", "avatar": "https://goo.gl/images/WqDt96", 
//						"role": "Player", "points": any positive integer}

		UserEntity actualValue = this.userService.getUser(email, playground);
		assertThat(actualValue).extracting("email", "playground", "username", "avatar", "role").containsExactly(email,
				playground, username, avatar, role);
	}

	// S
	@Test(expected = Exception.class)
	public void testUserSignupWithDuplicateKey() throws Exception {
		String url = base_url + "/playground/users";
		String email = "usermail2@usermail.com";
		String username = "user2";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Player";

//		 Given Server is up
//		And the database contains
//		[{"email": "usermail2@usermail.com", "playground": "2019a.Talin",
//				"username": "user2", "avatar": "https://goo.gl/images/WqDt96",
//						"role": "Player", "points": 0, "code":"X"}]

		this.userService.addNewUser(new UserEntity(email, username, avatar, role));

//		When I POST http://localhost:8083/playground/users with 
//		{"email":"usermail2@usermail.com", "username":"user2", 
//				"avatar":"https://goo.gl/images/WqDt96", "role":"Player"}
//		with headers:
//			Accept: application/json
//			Content-Type:  application/json

		NewUserForm newUserForm = this.jackson
				.readValue(
						"{" + "\"email\":\"usermail2@usermail.com\",\"username\":\"user2\","
								+ "\"avatar\":\"https://goo.gl/images/WqDt96\",\"role\":\"Player\"" + "}",
						NewUserForm.class);

		this.restTemplate.postForObject(url, newUserForm, NewUserForm.class);

//		Then the response status is <> 2xx

	}

	// S
	@Test
	public void testValidateSuccessfully() throws Exception {
		String url = base_url + "/playground/users/confirm/{playground}/{email}/{code}";
		String email = "usermail1@usermail.com";
		String username = "user1";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Manager";
		String code;
//		Given Server is up
//		And the database contains 
//		[{"email": "usermail1@usermail.com", "playground": "2019a.Talin",
//				"username": "user1", "avatar": "https://goo.gl/images/WqDt96", 
//						"role": "Manager", "points": 0,	"code":"X"}]
		UserEntity userEntity = new UserEntity(email, username, avatar, role);
		code = userEntity.getCode();
		this.userService.addNewUser(userEntity);
		
		
//		When I GET http://localhost:8083/playground/users/confirm/2019a.Talin/usermail1@usermail.com/X
//		with headers:
//			Accept: application/json

		UserTO actualUser = this.restTemplate.getForObject(url, UserTO.class, playground, email, code);

//		Then the response status is 2xx and body is 
//		{
//				"email": "usermail1@usermail.com"
//		    	"playground": "2019a.Talin",
//		  		"username": any valid user name,
//		   		"avatar": any valid url,
//		    	"role": Manager/Player,
//		   	 	"points": any positive integer
//		}

		assertThat(actualUser).extracting("email", "playground", "username", "avatar", "role").containsExactly(email,
				playground, username, avatar, role);

		// And the database contains for
//		email: "usermail1@usermail.com" and playground: 2019a.Talin
//		the object 
//		{"email": "usermail1@usermail.com", "playground": "2019a.Talin",
//				"username": "user1", "avatar": "https://goo.gl/images/WqDt96",
//						"role": "Manager", "points": 0, "code":null}

		UserEntity actualValue = this.userService.getUser(actualUser.getEmail(), actualUser.getPlayground());
		assertThat(actualValue).extracting("email", "playground", "username", "avatar", "role", "code")
				.containsExactly(email, playground, username, avatar, role, null);
	}

	// S
	@Test(expected = Exception.class)
	public void testValidateWithInvalidCode() throws Exception {
		String url = base_url + "/playground/users/confirm/{playground}/{email}/{code}";
		String email = "usermail1@usermail.com";
		String username = "user1";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Manager";
		String code = 1234 + "";
		String wrongCode = 1111 + "";
//		Given Server is up
//		And the database contains 
//		[{"email": "usermail1@usermail.com", "playground": "2019a.Talin",
//				"username": "user1", "avatar": "https://goo.gl/images/WqDt96", 
//						"role": "Manager", "points": 0,	"code":"X"}]

		this.userService.addNewUser(new UserEntity(email, username, avatar, role));
		this.userService.getUser(email, playground).setCode(code);

//		When I GET http://localhost:8083/playground/users/confirm/2019a.Talin/usermail1@usermail.com/Y
//		with headers:
//		 	Accept: application/json

		this.restTemplate.getForObject(url, UserTO.class, playground, email, wrongCode);

//		Then the response status is <> 2xx

	}

	// S
	@Test(expected = Exception.class)
	public void testValidateWithUnregisteredUser() throws Exception {
		String url = base_url + "/playground/users/confirm/{playground}/{email}/{code}";
		String email = "unregistered-user-mail@usermail.com";
		String code = 1234 + "";
//		Given Server is up

//		When I GET http://localhost:8083/playground/users/confirm/2019a.Talin/unregistered-user-mail@usermail.com/1234
//		with headers:
//			Accept: application/json

		this.restTemplate.getForObject(url, UserTO.class, playground, email, code);

//		Then the response status is <> 2xx

	}

	// S
	@Test
	public void testLoginSuccessfully() throws Exception {
		String url = base_url + "/playground/users/login/{playground}/{email}";
		String email = "usermail1@usermail.com";
		String username = "user1";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Manager";
		String code = 1234 + "";

//		Given Server is up
//		And the database contains 
//		[{"email": "usermail1@usermail.com", "playground": "2019a.Talin",
//				"username": "user1", "avatar": "https://goo.gl/images/WqDt96", 
//						"role": "Manager", "points": 0,	"code":null}]
		
		UserEntity userEntity = new UserEntity(email, username, avatar, role);
		this.userService.addNewUser(userEntity);
		this.userService.validateUser(userEntity, userEntity.getCode());
		
		

//		When I GET http://localhost:8083/playground/users/login/2019a.Talin/usermail1@usermail.com
//		with headers:
//		 Accept: application/json

		UserTO actualUser = this.restTemplate.getForObject(url, UserTO.class, playground, email);

//		Then the response status is 2xx and body is 
//		{
//				"email": "usermail1@usermail.com"
//		    	"playground": "2019a.Talin",
//		  		"username": any valid user name,
//		   		"avatar": any valid url,
//		    	"role": Manager/Player,
//		   	 	"points": any positive integer
//		}

		assertThat(actualUser).extracting("email", "playground").containsExactly(email, playground);
	}

	// S
	@Test(expected = Exception.class)
	public void testLoginWithUnconfirmedUser() throws Exception {
		String url = base_url + "/playground/users/login/{playground}/{email}";
		String email = "usermail1@usermail.com";
		String username = "user1";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Manager";

//		Given Server is up
//		And the database contains 
//		[{"email": "usermail1@usermail.com", "playground": "2019a.Talin",
//				"username": "user1", "avatar": "https://goo.gl/images/WqDt96", 
//						"role": "Manager", "points": 0,	"code":"X"}]

		this.userService.addNewUser(new UserEntity(email, username, avatar, role));

//		When I GET http://localhost:8083/playground/users/login/2019a.Talin/usermail1@usermail.com
//		with headers:
//		 Accept: application/json

		this.restTemplate.getForObject(url, UserTO.class, playground, email);

//		Then the response status is <> 2xx

	}

	// S
	@Test(expected = Exception.class)
	public void testLoginWithUnregisteredUser() throws Exception {
		String url = base_url + "/playground/users/login/{playground}/{email}";
		String email = "unregistered-user-mail@usermail.com";

//		Given Server is up

//		When I GET http://localhost:8083/playground/users/login/2019a.Talin/unregistered-user-mail@usermail.com
//		with headers:
//		 	Accept: application/json

		this.restTemplate.getForObject(url, UserTO.class, playground, email);

//		Then the response status is <> 2xx

	}

	private void setElementsDatabase(int numberOFElements) {


		String type = "animal";
		Map<String, Object> attributes = new HashMap<>();
		String creatorPlayground = "2019a.talin";
		String creatorEmail = "2019a.talin@Gmail.com";
		Date exirationDate = null;
		final String name;

		// location,value,exirationDate,type,attributes,creatorPlayground,creatorEmail
		// add specific attribute
		name = "cat";

		// location,value,exirationDate,type,attributes,creatorPlayground,creatorEmail
		IntStream.range(0, numberOFElements) // int stream
				.mapToObj(value -> new ElementEntity(new Location(value, value),
						(name == null) ? "animal #" + value : name, exirationDate, type, attributes, creatorPlayground,
						creatorEmail)) // ElementTO stream using constructor
										// reference
				.forEach(t -> {
					try {
						elementService.addNewElement(t);
					} catch (ElementAlreadyExistException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
	}

	// A
	@Test
	public void testGetAllElementsUsingPaginationWithDefaultSizeOfFirstPageSuccessfully() throws Exception {

		int DefaultSize = 10;
		String url = base_url + "/playground/elements/2019a.talin/talin@email.com/all";

		
		 * Given Server is up And the database contains 10 Elements
		 

		setElementsDatabase(100);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isNotNull().hasSize(DefaultSize);

	}

	// A
	@Test
	public void TestGetSomeElementsUsingPaginationSuccessfully() throws Exception {

		int size = 3;
		String url = base_url + "/playground/elements/2019a.talin/talin@email.com/all" + "?size=" + size;

		
		 * Given Server is up And the database contains 10 Elements
		 
		setElementsDatabase(10);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isNotNull().hasSize(size);

	}

	// A
	@Test
	public void TestGetNoElementsUsingPaginationOf100PageSuccessfully() throws Exception {

		int size = 3;
		int page = 100;

		String url = base_url + "/playground/elements/2019a.talin/talin@email.com/all" + "?size=" + size + "&page="
				+ page;

		
		 * Given Server is up And the database contains 10 Elements
		 
		setElementsDatabase(10);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isNotNull().hasSize(0);
	}

	// A
	@Test
	public void TestGetAllElementsUsingPaginationOfSecondPageSuccessfully() throws Exception {

		int size = 6;
		int page = 1;

		String url = base_url + "/playground/elements/2019a.talin/talin@email.com/all" + "?size=" + size + "&page="
				+ page;

		
		 * Given Server is up And the database contains 20 Elements
		 
		setElementsDatabase(20);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isNotNull().hasSize(size);
	}

	// A
	@Test(expected = Exception.class)
	public void testGetAllElementsWithIinvalidPageSize() {
		// when


		String url = base_url + "/playground/elements/null/talin@email.com/all";

		
		 * Given Server is up And the database contains 20 Elements
		 
		setElementsDatabase(20);

		// When I Get /playground/elements/null/talin@email.com/all?size=-6&page=1
		this.restTemplate.getForObject(url + "?size={size}&page={page}", ElementTO[].class, -6, 1);

		// Then the response status is <> 2xx
	}

	// A
	@Test
	public void TesTGetAllTheNearElementsUsingPaginationWithDefaultSizeOfFirstPageSuccessfully() throws Exception {
		int x = 10;
		int y = 10;
		int distance = 10;
		int DefaultSize = 10;

		String url = base_url + "/playground/elements/2019a.talin/Tali@email.com/near/" + x + "/" + y + "/" + distance;

		
		 * Given Server is up And the database contains 10 Elements
		 
		setElementsDatabase(100);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// how to check near Elements
		// Then the response status is 2xx and body contains 10 near Elements

		// then
		assertThat(actualElement).isNotNull().hasSize(DefaultSize);

	}

	// A
	@Test
	public void TestGetSomeOfTheNearElementsUsingPaginationSuccessfully() throws Exception {
		int x = 5;
		int y = 5;
		int distance = 10;
		int size = 3;

		
		String url = base_url + "/playground/elements/2019a.talin/Tali@email.com/near/" + x + "/" + y + "/" + distance
				+ "?size=" + size;

		
		 * Given Server is up And the database contains 10 Elements
		 
		setElementsDatabase(100);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// how to check near Elements
		// Then the response status is 2xx and body contains 3 near Elements

		// then
		assertThat(actualElement).isNotNull().hasSize(size);

	}

	// A
	@Test
	public void TestGetAllNearElementsUsingPaginationOfSecondPageSuccessfully() throws Exception {
		int x = 5;
		int y = 5;
		int distance = 10;
		int size = 5;
		int page = 1;

		String url = base_url + "/playground/elements/2019a.talin/Tali@email.com/near/" + x + "/" + y + "/" + distance
				+ "?size=" + size + "&page=" + page;

		
		 * Given Server is up And the database contains 10 Elements
		 
		setElementsDatabase(20);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// how to check near Elements
		// Then the response status is 2xx and body contains 3 near Elements

		// then
		assertThat(actualElement).isNotNull().hasSize(size);

	}

	// A
	@Test
	public void TestGetNoNearElementsUsingPaginationOf100PageSuccessfully() throws Exception {
		int x = 5;
		int y = 5;
		int distance = 10;
		int size = 3;
		int page = 100;

		String url = base_url + "/playground/elements/2019a.talin/Tali@email.com/near/" + x + "/" + y + "/" + distance
				+ "?size=" + size + "&page=" + page;

		
		 * Given Server is up And the database contains 10 Elements
		 

		setElementsDatabase(1);

		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isNotNull().hasSize(0);
	}

	// A
	@Test(expected = Exception.class)
	public void TestGetAllTheNearElementsWithInvalidPageSize() throws Exception {
		// when
		int x = 5;
		int y = 4;
		int distance = 10;

		String url = base_url + "/playground/elements/null/Tali@email.com/near/" + x + "/" + y + "/" + distance;

		this.restTemplate.getForObject(url + "?size={size}&page={page}", ElementTO[].class, -6, 1);
	}

	// A
	@Test
	public void updateAnElementSuccessfully() throws Exception {
		// Given Server is up


		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("Play", "Woof");

		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setCreationDate(null);

		elementEntity = this.elementService.addNewElement(elementEntity);
		
		
		
		System.out.println("\n\n\n\n\n " + elementEntity.getId());
		System.out.println(this.elementService.getElement(elementEntity.getId(),elementEntity.getPlayground()).toString());

		ElementTO updatedElementTO = new ElementTO();
		updatedElementTO.setId(elementEntity.getId());
		updatedElementTO.setPlayground(elementEntity.getPlayground());
		updatedElementTO.setLocation(new Location(10, 10));
		updatedElementTO.setName("Rex");
		updatedElementTO.setType("Dog");
		updatedElementTO.setAttributes(attributes);
		updatedElementTO.setCreationDate(null);
		
		
		
		String url = base_url + "/playground/elements/2019a.talin/talin@email.com/" + elementEntity.getPlayground() + "/"
				+ elementEntity.getId();


		this.restTemplate.put(url, updatedElementTO);

		ElementEntity actualElement = this.elementService.getElement(elementEntity.getId(), playground);

		ElementEntity expectedElement = new ElementEntity();
		expectedElement.setId(elementEntity.getId());
		expectedElement.setX(10.0);
		expectedElement.setY(10.0);
		expectedElement.setName("Rex");
		expectedElement.setType("Dog");
		Map<String, Object> attributesForexpectedElement = new HashMap<String, Object>();
		attributesForexpectedElement.put("Play", "Woof");
		expectedElement.setAttributes(attributesForexpectedElement);
		expectedElement.setCreationDate(null);

		// String actualElementJson = this.jackson.writeValueAsString(actualElement);
		// String expectedMessageJson =
		// this.jackson.writeValueAsString(expectedElement);

		assertThat(actualElement).isNotNull().isEqualTo(expectedElement);

	}

	// A
	@Test(expected = Exception.class)
	public void testUpdateNonExistingElement() throws Exception {
		String url = base_url + "/playground/elements/2019a.talin/talin@email.com/2019a.talin/{ID}";
		String ID = "0";

		Map<String, Object> attributesForEntityInDataBase = new HashMap<String, Object>();
		// Given server is up

		// When I Put
		ElementTO updatedElementTO = new ElementTO();
		updatedElementTO.setId(ID);
		updatedElementTO.setLocation(new Location(10, 10));
		updatedElementTO.setName("Rex");
		updatedElementTO.setType("Dog");
		updatedElementTO.setAttributes(attributesForEntityInDataBase);
		updatedElementTO.setCreationDate(null);

		this.restTemplate.put(url, updatedElementTO, ID);

		// Then the response status <> 2xx
	}

	// I
	@Test
	public void TestUpdateUserSuccessfully() throws Exception {

		String url = base_url + "/playground/users/{playground}/{email}";

		String email = "talin@email.com";
		String username = "user2";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Player";
		String newAvatar = "https://moodle.afeka.ac.il/theme/image.jpg";
		// Given server is up
		// And the database contains And the user database contains {"email":
		// ”talin@email.com”,"playground": "2019a.Talin",
		// "username": "user2","avatar": "https://goo.gl/images/WqDt96",
		// "role": "Player","points": 0,"code":null}
		UserEntity userEnti = new UserEntity(email, username, avatar, role);
		userEnti.setCode(null);
		this.userService.addNewUser(userEnti);

		// When I Put http://localhost:8083/playground/users/2019a.talin/talin@email.com
		// And with body
		// {
		// "email": ”talin@email.com”
		// "playground": "2019a.Talin",
		// "username": "user2",
		// "avatar": “https://moodle.afeka.ac.il/theme/image.jpg",
		// "role": "Player",
		// "points": 0
		// }
		// with headers:
		// Accept: application/json
		// Content-Type: application/json
		UserTO updateUser = new UserTO();
		updateUser.setRole(role);
		updateUser.setUsername(username);
		updateUser.setAvatar(newAvatar);

		this.restTemplate.put(url, updateUser, playground, email);

		// Then the response status is 200
		// And the database contains for email: ”talin@email.com” the object
		// {"email": ”talin@email.com”,"playground": "2019a.Talin",
		// "username":"user2","avatar":“https://moodle.afeka.ac.il/theme/image.jpg",
		// "role": "Player","points": 0,"code":"1234"
		// }
		UserEntity actualUser = this.userService.getUser(email, playground);

		assertThat(actualUser).extracting("email", "playground", "username", "avatar", "role", "code")
				.containsExactly(email, playground, username, newAvatar, role, null);
	}

	// I
	@Test(expected = Exception.class)
	public void TestUpdateNonExistingUser() throws Exception {
		String url = base_url + "/playground/users/{playground}/{email}";
		String email = "talin@email.com";
		String username = "user2";
		String role = "Player";
		String newAvatar = "https://moodle.afeka.ac.il/theme/image.jpg";

		// Given Server is up
		// When I Put http://localhost:8083/playground/users/2019a.talin/talin@email.com
		// And with body
		// {
		// "email": ”talin@email.com”
		// "playground": "2019a.Talin",
		// "username": "user2",
		// "avatar": “https://moodle.afeka.ac.il/theme/image.jpg",
		// "role": "Player",
		// "points": 0
		// }
		// with headers:
		// Accept: application/json
		// Content-Type: application/json

		UserTO updateUser = new UserTO();
		updateUser.setEmail(email);
		updateUser.setRole(role);
		updateUser.setUsername(username);
		updateUser.setAvatar(newAvatar);

		this.restTemplate.put(url, updateUser, playground, email);

		// Then the response status is <> 2xx
	}

	// I
		@Test
		public void TestElementCreatedSuccessfully() throws Exception {
			// Given Server is up

			String url = base_url + "/playground/elements/{userPlayground}/{email}";
			String email = "tali@mali.com";
			String name = "cat";
			double x = 1.0;
			double y = 1.0;
			String type = "animal";
			Map<String, Object> attributes = new HashMap<>();

			ElementTO newElement = new ElementTO();
			newElement.setName(name);
			newElement.setLocation(new Location(x, y));
			newElement.setType(type);
			newElement.setAttributes(attributes);

			// Given server is up

			// When I POST
			// http://localhost:8083/playground/elements/2019a.talin/talin@email.com
			// And with body
			// {
			// “name”: “cat”,
			// “type”:”animal”
			// “location”:{“x”:0.0,”y”:0.0},
			// "attributes": {}
			// }
			// with headers:
			// Accept: application/json
			// Content-Type: application/json
			ElementTO elementAfterPost = this.restTemplate.postForObject(url, newElement, ElementTO.class, playground, email);
			// Then the response status is 2xx
			// and the database contains for playground+id:“2019a.talin0”

			ElementEntity elementEntityExist = this.elementService.getElement(elementAfterPost.getId(), playground);

			assertThat(elementEntityExist).extracting("name", "type", "x","y","attributes").containsExactly(name, type,
					x,y, attributes);
		}

	// I
	@Test
	public void TestGetElementSuccessfully() throws Exception {
		// Given Server is up
		// And the database contains
		// {
		// “playground”: 2019a.talin”
		// “ id”: 123
		// "location": {
		// "x": 0,
		// "y": 0
		// },
		// "name": "cat",
		// "type": "Animal",
		// "attributes": {},
		// "creatorPlayground":"2019a.talin",
		// "creatorEmail":”Talin@email.com"
		// }
		String url = base_url + "/playground/elements/{userPlayground}/{email}/{playground}/{id}";
		String id = "123";
		String email = "tali@mali.com";
		String name = "cat";
		double x = 0.0;
		double y = 0.0;
		String type = "animal";

		ElementEntity newElement = new ElementEntity();
		newElement.setName(name);
		newElement.setX(x);
		newElement.setX(y);

		newElement.setType(type);

		newElement = this.elementService.addNewElement(newElement);

		
		id = newElement.getId();
		// When I Get
		// http://localhost:8083/playground/elements/2019a.talin/talin@email.com/2019a.talin/123
		// with headers:
		// Accept: application/json

		ElementTO actualElement = this.restTemplate.getForObject(url, ElementTO.class, playground, email, playground,
				id);

		// Then the response status is 2xx and body is

		// {
		// “playground”: 2019a.talin”
		// “ id”: 123
		// "location": {
		// "x": 0,
		// "y": 0
		// },
		// "name": "Animal", "type": "Animal", "attributes": {},
		// "creatorPlayground":"2019a.talin", "creatorEmail":"Talin@email.com"
		// }
		assertThat(actualElement).isNotNull().extracting("playground", "id", "name", "type", "location", "attributes")
				.containsExactly(playground, id, name, type, new Location(x, y), new HashMap<>());
	}

	// I
	@Test(expected = Exception.class)
	public void TestGetElementWithInvalidId() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/elements/{userPlayground}/{email}/{playground}/{id}";
		String id = "123";
		String email = "tali@mali.com";

		// When I GET
		// http://localhost:8083/playground/elements/2019a.talin/null/2019a.talin/0
		// with headers:
		// Accept: application/json
		ElementTO actualElement = this.restTemplate.getForObject(url, ElementTO.class, playground, email, playground,
				id);
		// Then the response status is <> 2xx
	}

	// T
	@Test
	public void testGetEementsWithThisAttributeValueUsingDefaultPagination() throws Exception {

		int DefaultSize = 10;
		String url = base_url + "/playground/elements/2019a.talin/myEmail@mail.com/search/name/cat";

		
		 * Given Server is up And the database contains 20 elements with the name cat
		 

		setElementsDatabase(20);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isNotNull().hasSize(DefaultSize);
	}

	// T
	@Test
	public void testGetElementsWithAttributeValueThatNotExistsUsingDefaultPagination() throws Exception {

		String url = base_url + "/playground/elements/2019a.talin/myEmail@mail.com/search/name/cat";

		
		 * Given Server is up
		 

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isEmpty();
	}

	// T
	@Test(expected = Exception.class)
	public void testGetElementsWithInvalidAttributeUsingDefaultPagination() throws Exception {
		int DefaultSize = 10;

		String url = base_url + "/playground/elements/2019a.talin/myEmail@mail.com/search/Momo/cat";

		
		 * Given Server is up
		 

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).hasSize(DefaultSize);
	}

	// T
	@Test
	public void testGetElementsWithThisAttributeValueUsingPaginationSuccessfully() throws Exception {

		int size = 3;
		String url = base_url + "/playground/elements/2019a.talin/myEmail@mail.com/search/name/cat" + "?size=" + size;

		
		 * Given Server is up And the database contains 10 elements with the name cat
		 
		setElementsDatabase(10);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isNotNull().hasSize(size);
	}

	// T
	@Test
	public void testGetElementsWithThisAttributeValueUsingPaginationOf100PageSuccessfully() throws Exception {

		int size = 6;
		int page = 100;

		String url = base_url + "/playground/elements/2019a.talin/myEmail@mail.com/search/name/cat" + "?size=" + size
				+ "&page=" + page;

		
		 * Given Server is up And the database contains 10 elements with the name cat
		 
		setElementsDatabase(10);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isEmpty();
	}

	// T
	@Test
	public void testGetElementsWithThisAttributeValueUsingPaginationOfSecondPageSuccessfully() throws Exception {

		int size = 6;
		int page = 1;
		int numOfElements = 10;

		String url = base_url + "/playground/elements/2019a.talin/myEmail@mail.com/search/name/cat" + "?size=" + size
				+ "&page=" + page;

		
		 * Given Server is up And the database contains 10 elements with the name cat
		 
		setElementsDatabase(10);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class);

		// then
		assertThat(actualElement).isNotNull().hasSize(numOfElements - size);
	}

	// T
	@Test(expected = Exception.class)
	public void testGetElementsWithThisAttributeValueWithInvalidPageSize() {
		// when
		String url = base_url + "/playground/elements/2019a.talin/myEmail@mail.com/search/name/cat";

		
		 * Given Server is up
		 

		// When
		this.restTemplate.getForObject(url + "?size={size}&page={page}", ElementTO[].class, -6, 1);

		// Then the response status is <> 2xx
	}

	@Test
	public void testActivateElementSuccessfully() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/activities/2019a.talin/myEmail@mail.com";

		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity elementEntity = this.elementService.addNewElement(new ElementEntity());

		// When I POST activity with
		String elementId = elementEntity.getId();
		String elementPlayground = playground;
		String type = "ACO";

		// check that element exists
		this.elementService.getElement(elementId, elementPlayground);

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		ActivityTO activityAfterPost = this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class);

		// Then the response status is 2xx and body is:
		ActivityEntity activityEntityExist = this.activityService.getActivity(activityAfterPost.getId(),
				playground);
		
		assertThat(activityEntityExist).extracting("elementId", "elementPlayground", "type").
		containsExactly(elementId, elementPlayground, type);
	}


	@Test(expected = Exception.class)
	public void testActivateElementWithInvalidActivityType() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/activities/2019a.talin/myEmail@mail.com";

		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity elementEntity = this.elementService.addNewElement(new ElementEntity());

		// When I POST activity with
		String elementId = elementEntity.getId();
		String elementPlayground = playground;
		String type = "Play";

		// check that element exists
		this.elementService.getElement(elementId, elementPlayground);

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class);

		// Then the response status is <> 2xx
	}


	@Test(expected = Exception.class)
	public void testActivatingNotExistingElement() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/activities/2019a.talin/myEmail@mail.com";

		// When I POST activity with
		String elementId = "0";
		String elementPlayground = playground;
		String type = "ACO";

		// check that element exists
		this.elementService.getElement(elementId, elementPlayground);

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class);

		// Then the response status is <> 2xx
	}
}
*/