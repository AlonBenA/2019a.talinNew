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
import org.springframework.web.client.RestTemplate;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Services.PlaygroundActivityService;
import playground.logic.Services.PlaygroundElementService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestsActivity {
	@Autowired
	private PlaygroundActivityService activityService;

	@Autowired
	private PlaygroundElementService elementService;

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

	
	@Test
	public void testActivateElementSuccessfully() throws Exception {
		
		String userEmail = "talin@email.com";
		String userPlayground = "2019a.talin"; 
		
		// Given Server is up
		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity elementEntity = this.elementService.addNewElement(userPlayground, userEmail, new ElementEntity());

		// When I POST activity with
		String elementId = elementEntity.getId();
		String elementPlayground = playground;
		String type = "ACO";

		// check that element exists
		this.elementService.getElement(userPlayground, userEmail, elementId, elementPlayground);

		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		ActivityTO activityAfterPost = this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class, 
				userPlayground, userEmail);

		// Then the response status is 2xx and body is:
		ActivityEntity activityEntityExist = this.activityService.getActivity(userPlayground, userEmail, activityAfterPost.getId(),
				playground);
		
		assertThat(activityEntityExist).extracting("elementId", "elementPlayground", "type").
		containsExactly(elementId, elementPlayground, type);
	}



	@Test(expected = Exception.class)
	public void testActivateElementWithInvalidActivityType() throws Exception {
		
		String userEmail = "talin@email.com";
		String userPlayground = "2019a.talin";
		
		
		// Given Server is up
		String url = base_url + "/playground/activities/{userPlayground}/{email}";

		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity elementEntity = this.elementService.addNewElement(userPlayground, userEmail, new ElementEntity());

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
		
		String userEmail = "talin@email.com";
		String userPlayground = "2019a.talin";
		
		// Given Server is up
		String url = base_url + "/playground/activities/{userPlayground}/{email}";

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
