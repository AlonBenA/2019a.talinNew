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

import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Services.PlaygroundActivityService;
import playground.logic.Services.PlaygroundElementService;
import playground.logic.Services.PlaygroundService;

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
		this.activityService.cleanup();
		this.elementService.cleanup();
	}

	// T
	@Test
	public void testActivateElementSuccessfully() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/activities/2019a.talin/myEmail@mail.com";

		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity.resetID();
		this.elementService.addNewElement(new ElementEntity());

		// When I POST activity with
		String elementId = "0";
		String elementPlayground = "2019a.talin";
		String type = "ACO";

		// check that element exists
		this.elementService.getElement(elementId, elementPlayground);

		ActivityTO.resetID();
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class);

		// Then the response status is 2xx and body is:
		ActivityEntity activityEntityExist = this.activityService.getActivity("0", "2019a.talin");
		ActivityTO activityTOExist = new ActivityTO(activityEntityExist);
		ActivityTO expectedTOActivity = this.jackson.readValue("{\"playground\":\"2019a.talin\", \"id\":\"0\","
				+ " \"elementPlayground\":\"2019a.talin\", \"elementId\":\"0\","
				+ " \"type\":\"ACO\", \"playerPlayground\":\"2019a.talin\","
				+ " \"playerEmail\": \"myEmail@mail.com\"}", ActivityTO.class);

		assertThat(activityTOExist).isEqualTo(expectedTOActivity);
	}

	// T
	@Test(expected = Exception.class)
	public void testActivateElementWithInvalidActivityType() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/activities/2019a.talin/myEmail@mail.com";

		// And the database contains element with playground+id: 2019a.talin0
		ElementEntity.resetID();
		this.elementService.addNewElement(new ElementEntity());

		// When I POST activity with
		String elementId = "0";
		String elementPlayground = "2019a.talin";
		String type = "Play";

		// check that element exists
		this.elementService.getElement(elementId, elementPlayground);

		ActivityTO.resetID();
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class);

		// Then the response status is <> 2xx
	}

	// T
	@Test(expected = Exception.class)
	public void testActivatingNotExistingElement() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/activities/2019a.talin/myEmail@mail.com";

		// When I POST activity with
		String elementId = "0";
		String elementPlayground = "2019a.talin";
		String type = "ACO";

		// check that element exists
		this.elementService.getElement(elementId, elementPlayground);

		ActivityTO.resetID();
		ActivityTO newActivityTO = new ActivityTO();
		newActivityTO.setElementId(elementId);
		newActivityTO.setElementPlayground(elementPlayground);
		newActivityTO.setType(type);
		this.restTemplate.postForObject(url, newActivityTO, ActivityTO.class);

		// Then the response status is <> 2xx
	}
}
