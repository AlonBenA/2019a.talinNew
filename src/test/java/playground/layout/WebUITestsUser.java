package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

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

import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundUserService;
import playground.logic.jpa.JpaUserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestsUser {

	@Autowired
	private PlaygroundUserService userService;
	
//					DB
//	@Autowired
//	private JpaUserService userService;

	private RestTemplate restTemplate;

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
		this.userService.cleanup();
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
		UserTO userTo = this.restTemplate.postForObject(url, newUserForm, UserTO.class);

//		  with headers:
//		  	Accept: application/json
//		 	Content-Type:  application/json	
//		 Then the response status is 2xx and body is 
//		 {"email": "usermail2@usermail.com", "playground": "2019a.Talin", 
//				"username": "user2", "avatar": "https://goo.gl/images/WqDt96", 
//						"role": "Player", "points": any positive integer}

		UserEntity actualValue = this.userService.getUser(userTo.getEmail(),userTo.getPlayground());
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
		String code = 1234 + "";
//		Given Server is up
//		And the database contains 
//		[{"email": "usermail1@usermail.com", "playground": "2019a.Talin",
//				"username": "user1", "avatar": "https://goo.gl/images/WqDt96", 
//						"role": "Manager", "points": 0,	"code":"X"}]

		this.userService.addNewUser(new UserEntity(email, username, avatar, role));
		this.userService.getUser(email, playground).setCode(code);

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

		this.userService.addNewUser(new UserEntity(email, username, avatar, role));
		UserEntity userEntity = this.userService.getUser(email, playground);
		userEntity.setCode(code);
		userEntity.verify(code);

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
				.containsExactly(email, playground, username, newAvatar, role, "null");
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

}
