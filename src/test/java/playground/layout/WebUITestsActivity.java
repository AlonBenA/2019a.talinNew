package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Email;

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
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundActivityService;
import playground.logic.Services.PlaygroundElementService;
import playground.logic.Services.PlaygroundUserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestsActivity {
	@Autowired
	private PlaygroundActivityService activityService;

	@Autowired
	private PlaygroundElementService elementService;
	
	@Autowired
	private PlaygroundUserService userService;

	private RestTemplate restTemplate;

	private String playground;

	
	@Value("${playground}")	//set playground as "2019a.talin"
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
		this.activityService.cleanup();
		this.elementService.cleanup();
	}
	
	///TODO: REPLACE LATER WITH TEST HELPER FUNCTIONS
	private void createMangerAccount(String userEmail,String userPlayground)
	{
		String username = "user1";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Manager";
		UserEntity user = new UserEntity(userEmail, username, avatar, role);
		user.setPlayground(userPlayground);
		
		//add the user to the database
		user = userService.addNewUser(user);
		
		//validate user to the database
		userService.validateUser(user, user.getCode());
		
	}
	
	private void createPlayerAccount(String userEmail,String userPlayground)
	{
		String username = "user1";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Player";
		UserEntity user = new UserEntity(userEmail, username, avatar, role);
		user.setPlayground(userPlayground);
		
		//add the user to the database
		user = userService.addNewUser(user);
		
		//validate user to the database
		userService.validateUser(user, user.getCode());
	}

	
	@Test
	public void testActivateElementSuccessfully() throws Exception {	
		// Given Server is up
		String url = base_url + "/playground/activities/{userPlayground}/{email}";
		
		//create Manger to add element
		String userEmail = "userM@email.com";
		String userPlayground = "2019a.talin";
		createMangerAccount(userEmail, userPlayground);
		

		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity elementEntity = this.elementService.addNewElement(userPlayground, userEmail, new ElementEntity());

		//create Player to post Activity
		userEmail = "talinPlayer1@email.com";
		userPlayground = "2019a.talin";
		createPlayerAccount(userEmail, userPlayground);
		
		// When I POST activity with
		String elementId = elementEntity.getId();
		String elementPlayground = playground;
		String type = "ECHO";

		// check that element exists
		this.elementService.getElement(userPlayground, userEmail, elementId, elementPlayground);

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		try {
			ActivityTO activityAfterPost = this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class, 
					userPlayground, userEmail);
			// Then the response status is 2xx and body is:
					ActivityEntity activityEntityExist = this.activityService.getActivity(userPlayground, userEmail, activityAfterPost.getId(),
							playground);
					
					assertThat(activityEntityExist).extracting("elementId", "elementPlayground", "type").
					containsExactly(elementId, elementPlayground, type);
				
		}catch (HttpServerErrorException e) {
			System.err.println(e.getResponseBodyAsString());
			throw e;
		}
	}



	@Test(expected = Exception.class)
	public void testActivateElementWithInvalidActivityType() throws Exception {
		
		// Given Server is up
		String url = base_url + "/playground/activities/{userPlayground}/{email}";
		
		//create Manger to add element
		String userEmail = "manager@email.com";
		String userPlayground = "2019a.talin";
		createMangerAccount(userEmail, userPlayground);
		
		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity elementEntity = this.elementService.addNewElement(userPlayground, userEmail, new ElementEntity());

		//create Player to post Activity
		userEmail = "player@email.com";
		userPlayground = "2019a.talin";
		createPlayerAccount(userEmail, userPlayground);
		
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

		//create Player to post Activity
		String userEmail = "player2@email.com";
		String userPlayground = "2019a.talin";
		createPlayerAccount(userEmail, userPlayground);
		
		// When I POST activity with
		String elementId = "0";
		String elementPlayground = playground;
		String type = "ECHO";

		// check that element exists
		this.elementService.getElement(userPlayground, userEmail, elementId, elementPlayground);

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class, playground, userEmail);

		// Then the response status is <> 2xx
	}
}
