package playground.layout;

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
import playground.logic.Entities.ElementEntity;
import playground.logic.Services.PlaygroundElementService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestsElement {
	
	@Autowired
	private PlaygroundElementService elementService;
	

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
		this.elementService.cleanup();
	}


	private void setElementsDatabase(int numberOFElements) {

		Date exirationDate = null;
		String type = "animal";
		Map<String, Object> attributes = new HashMap<>();
		String creatorPlayground = "2019a.talin";
		String creatorEmail = "2019a.talin@Gmail.com";
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
				.forEach(elementService::addNewElement);
	}

	// A
	@Test
	public void testGetAllElementsUsingPaginationWithDefaultSizeOfFirstPageSuccessfully() throws Exception {

		int DefaultSize = 10;
		String url = base_url + "/playground/elements/2019a.talin/talin@email.com/all";

		/*
		 * Given Server is up And the database contains 10 Elements
		 */

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

		/*
		 * Given Server is up And the database contains 10 Elements
		 */
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

		/*
		 * Given Server is up And the database contains 10 Elements
		 */
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

		/*
		 * Given Server is up And the database contains 20 Elements
		 */
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

		/*
		 * Given Server is up And the database contains 20 Elements
		 */
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

		/*
		 * Given Server is up And the database contains 10 Elements
		 */
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

		/*
		 * Given Server is up And the database contains 10 Elements
		 */
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

		/*
		 * Given Server is up And the database contains 10 Elements
		 */
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

		/*
		 * Given Server is up And the database contains 10 Elements
		 */

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

		String updateAnElementID = "updateAnElementID";

		String url = base_url + "/playground/elements/2019a.talin/talin@email.com/" + playground + "/"
				+ updateAnElementID;

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("Play", "Woof");

		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setId(updateAnElementID);
		elementEntity.setCreationDate(null);

		this.elementService.addNewElement(elementEntity);

		ElementTO updatedElementTO = new ElementTO();
		updatedElementTO.setId(updateAnElementID);
		updatedElementTO.setPlayground(playground);
		updatedElementTO.setLocation(new Location(10, 10));
		updatedElementTO.setName("Rex");
		updatedElementTO.setType("Dog");
		updatedElementTO.setAttributes(attributes);
		updatedElementTO.setCreationDate(null);

		this.restTemplate.put(url, updatedElementTO);

		ElementEntity actualElement = this.elementService.getElement(updateAnElementID, playground);

		ElementEntity expectedElement = new ElementEntity();
		expectedElement.setId(updateAnElementID);
		expectedElement.setLocation(new Location(10, 10));
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
	public void TestElementCreatedSuccessfully() throws Exception {
		// Given Server is up

		String url = base_url + "/playground/elements/{userPlayground}/{email}";
		String Id = "123";
		String email = "tali@mali.com";
		String name = "cat";
		double x = 1.0;
		double y = 1.0;
		String type = "animal";
		Map<String, Object> attributes = new HashMap<>();

		ElementTO newElement = new ElementTO();
		newElement.setId(Id);
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
		this.restTemplate.postForObject(url, newElement, ElementTO.class, playground, email);
		// Then the response status is 2xx
		// and the database contains for playground+id:“2019a.talin0”

		ElementEntity elementEntityExist = this.elementService.getElement(Id, playground);

		assertThat(elementEntityExist).extracting("name", "type", "location", "attributes").containsExactly(name, type,
				new Location(x, y), attributes);
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
		newElement.setId(id);
		newElement.setName(name);
		newElement.setLocation(new Location(x, y));
		newElement.setType(type);

		this.elementService.addNewElement(newElement);

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

		/*
		 * Given Server is up And the database contains 20 elements with the name cat
		 */

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

		/*
		 * Given Server is up
		 */

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

		/*
		 * Given Server is up
		 */

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

		/*
		 * Given Server is up And the database contains 10 elements with the name cat
		 */
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

		/*
		 * Given Server is up And the database contains 10 elements with the name cat
		 */
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

		/*
		 * Given Server is up And the database contains 10 elements with the name cat
		 */
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

		/*
		 * Given Server is up
		 */

		// When
		this.restTemplate.getForObject(url + "?size={size}&page={page}", ElementTO[].class, -6, 1);

		// Then the response status is <> 2xx
	}
}
