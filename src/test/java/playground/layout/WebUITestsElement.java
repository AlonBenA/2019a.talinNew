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
import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundElementService;
import playground.logic.Services.PlaygroundUserService;
import playground.logic.jpa.ElementAlreadyExistException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebUITestsElement {
	
	@Autowired
	private PlaygroundElementService elementService;
	
	@Autowired
	private PlaygroundUserService userService;
	

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
		// cleanup databases
		this.elementService.cleanup();
		this.userService.cleanup();
	}
	
	
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


	private void setElementsDatabase(int numberOFElements) {

		Date exirationDate = null;
		String type = "animal";
		Map<String, Object> attributes = new HashMap<>();
		String creatorPlayground = "2019a.talin";
		String creatorEmail = "2019a.talin@Gmail.com";
		final String name;
		
		//Create the manger to add the elements
		createMangerAccount(creatorEmail, creatorPlayground);

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
						elementService.addNewElement(creatorPlayground,creatorEmail,t);
					} catch (ElementAlreadyExistException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
	}

	// A
	@Test
	public void TestGetAllTheElementsUsingPaginationWithDefaultSizeOfFirstPageWithValidPlayerAccountSuccessfully() throws Exception {

		int DefaultSize = 10;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/all";

		/*
		 * Given Server is up And the database contains 100 Elements
		 */

		setElementsDatabase(100);
		
		/*
		 * And the database contains player
		 */
		
		createPlayerAccount(userEmail, userPlayground);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail);

		// then
		assertThat(actualElement).isNotNull().hasSize(DefaultSize);

	}

	// A
	@Test
	public void TestGetSomeElementsUsingPaginationWithValidPlayerAccountSuccessfully() throws Exception {

		int size = 3;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/all?size={size}";

		/*
		 * Given Server is up And the database contains 10 Elements
		 */
		setElementsDatabase(10);
		
		/*
		 * And the database contains player
		 */
		
		createPlayerAccount(userEmail, userPlayground);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail,size);

		// then
		assertThat(actualElement).isNotNull().hasSize(size);

	}

	// A
	@Test
	public void TestGetNoElementsUsingPaginationOf100PageWithValidPlayerAccountSuccessfully() throws Exception {

		int size = 3;
		int page = 100;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/all?size={size}&page={page}";

		/*
		 * Given Server is up And the database contains 10 Elements
		 */
		setElementsDatabase(10);
		
		/*
		 * And the database contains player
		 */
		
		createPlayerAccount(userEmail, userPlayground);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail,size,page);

		// then
		assertThat(actualElement).isNotNull().hasSize(0);
	}

	// A
	@Test
	public void TestGetAllElementsUsingPaginationOfSecondPageAndValidMangerAccountSuccessfully() throws Exception {

		int size = 6;
		int page = 1;
		String userEmail = "Manger@Gmail.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/all?size={size}&page={page}";

		/*
		 * Given Server is up And the database contains 20 Elements
		 */
		setElementsDatabase(20);
		
		/*
		 * And the database contains manger
		 */
		
		createMangerAccount(userEmail, userPlayground);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail,size,page);

		// then
		assertThat(actualElement).isNotNull().hasSize(size);
	}

	// A
	@Test(expected = Exception.class)
	public void testGetAllElementsWithIinvalidPageSizeAndValidMangerAccount() {
		// when

		String userEmail = "Manger@Gmail.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/all";

		/*
		 * Given Server is up And the database contains 20 Elements
		 */
		setElementsDatabase(20);
		
		/*
		 * And the database contains manger
		 */
		
		createMangerAccount(userEmail, userPlayground);

		// When I Get /playground/elements/null/talin@email.com/all?size=-6&page=1
		this.restTemplate.getForObject(url + "?size={size}&page={page}", ElementTO[].class,userPlayground,userEmail, -6, 1);

		// Then the response status is <> 2xx
	}
	
	// A
	@Test(expected = Exception.class)
	public void TestGetAllTheElementsUsingPaginationWithDefaultSizeOfFirstPageWithInvalidPlayerAccount() {
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/all";

		/*
		 * Given Server is up And the database contains 100 Elements
		 */

		setElementsDatabase(10);

		// when
		this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail);

		// Then the response status is <> 2xx
	}

	// A
	@Test
	public void TesTGetAllTheNearElementsWithValidPlayerAccountUsingPaginationWithDefaultSizeOfFirstPageSuccessfully() throws Exception {
		
		int x = 10;
		int y = 10;
		int distance = 10;
		int DefaultSize = 10;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/near/{x}/{y}/{distance}";

		/*
		 * Given Server is up And the database contains 100 Elements
		 */
		setElementsDatabase(100);
		
		/*
		 * And the database contains player
		 */
		
		createPlayerAccount(userEmail, userPlayground);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail,x,y,distance);

		// how to check near Elements
		// Then the response status is 2xx and body contains 10 near Elements

		// then
		assertThat(actualElement).isNotNull().hasSize(DefaultSize);

	}

	// A
	@Test
	public void TestGetSomeOfTheNearElementsWithValidPlayerAccountUsingPaginationSuccessfully() throws Exception {
		int x = 5;
		int y = 5;
		int distance = 10;
		int size = 3;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/near/{x}/{y}/{distance}?size={size}";

		/*
		 * Given Server is up And the database contains 100 Elements
		 */
		setElementsDatabase(100);
		
		/*
		 * And the database contains player
		 */
		
		createPlayerAccount(userEmail, userPlayground);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail,
				x,y,distance,size);

		// Then the response status is 2xx and body contains 3 near Elements

		// then
		assertThat(actualElement).isNotNull().hasSize(size);

	}

	// A
	@Test
	public void TestGetAllNearElementsWithValidPlayerAccountUsingPaginationOfSecondPageSuccessfully() throws Exception {
		int x = 5;
		int y = 5;
		int distance = 10;
		int size = 5;
		int page = 1;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		String url =  base_url + "/playground/elements/{userPlayground}/{userEmail}/near/{x}/{y}/{distance}?size={size}&page={page}";

		/*
		 * Given Server is up And the database contains 20 Elements
		 */
		setElementsDatabase(20);
		
		
		/*
		 * And the database contains player
		 */
		
		createPlayerAccount(userEmail, userPlayground);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail,x,y,distance,size,page);

		// Then the response status is 2xx and body contains 3 near Elements

		// then
		assertThat(actualElement).isNotNull().hasSize(size);

	}

	// A
	@Test
	public void TestGetNoNearElementsWithValidPlayerAccountUsingPaginationOf100PageSuccessfully() throws Exception {
		int x = 5;
		int y = 5;
		int distance = 10;
		int size = 3;
		int page = 100;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		String url =  base_url + "/playground/elements/{userPlayground}/{userEmail}/near/{x}/{y}/{distance}?size={size}&page={page}";


		/*
		 * Given Server is up And the database contains 1 Element
		 */

		setElementsDatabase(1);
		
		/*
		 * And the database contains player
		 */
		createPlayerAccount(userEmail, userPlayground);
		

		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail
				,x,y,distance,size,page);

		// then
		assertThat(actualElement).isNotNull().hasSize(0);
	}

	// A
	@Test(expected = Exception.class)
	public void TestGetAllTheNearElementsWithInvalidPageSizeAndValidPlayerAccount() throws Exception {
		// when
		int x = 5;
		int y = 4;
		int distance = 10;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		
		/*
		 * Given Server is up And the database contains 20 Elements
		 */

		setElementsDatabase(20);
		
		/*
		 * And the database contains player
		 */
		createPlayerAccount(userEmail, userPlayground);
		
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/near/{x}/{y}/{distance}";

		this.restTemplate.getForObject(url + "?size={size}&page={page}", ElementTO[].class,userPlayground,userEmail,x,y,distance, -6, 1);
	}
	
	
	// A
	@Test(expected = Exception.class)
	public void TestGetAllTheNearElementsWithInvalidPlayerAccountUsingPaginationWithDefaultSizeOfFirstPage() throws Exception {
		int x = 10;
		int y = 10;
		int distance = 10;
		int DefaultSize = 10;
		String userEmail = "user@email.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/near/{x}/{y}/{distance}";

		/*
		 * Given Server is up And the database contains 100 Elements
		 */
		setElementsDatabase(100);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class,userPlayground,userEmail,x,y,distance);

		// how to check near Elements
		// Then the response status is 2xx and body contains 10 near Elements

		// then
		assertThat(actualElement).isNotNull().hasSize(DefaultSize);
	}

	// A
	@Test
	public void updateAnElementSuccessfully() throws Exception {
		// Given Server is up
		String userEmail = "Manger@mail.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/{Playground}/{Id}";

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("Play", "Woof");

		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setCreatorEmail(userEmail);
		elementEntity.setCreatorPlayground(userPlayground);
		elementEntity.setCreationDate(null);
		
		
		//database contains an manger:
		createMangerAccount(userEmail, userPlayground);
		
		//And the database contains an Element

		elementEntity = this.elementService.addNewElement(elementEntity.getCreatorPlayground(),elementEntity.getCreatorEmail(),elementEntity);
		
		
		//When I Put http://localhost:8083/playground/elements/2019a.talin/Manger@mail.com/2019a.talin/ElementID
		
		ElementTO updatedElementTO = new ElementTO();
		updatedElementTO.setId(elementEntity.getId());
		updatedElementTO.setPlayground(elementEntity.getPlayground());
		updatedElementTO.setLocation(new Location(10, 10));
		updatedElementTO.setName("Rex");
		updatedElementTO.setType("Dog");
		updatedElementTO.setAttributes(attributes);
		updatedElementTO.setCreationDate(null);
		

		this.restTemplate.put(url, updatedElementTO,userPlayground,userEmail,elementEntity.getPlayground(),elementEntity.getId());

		//Then the response status is 200 
		//And the database contains

		
		ElementEntity actualElement = this.elementService.getElement(elementEntity.getCreatorPlayground(),elementEntity.getCreatorEmail(),elementEntity.getId(), playground);

		ElementEntity expectedElement = new ElementEntity();
		expectedElement.setId(elementEntity.getId());
		expectedElement.setCreatorEmail(userEmail);
		expectedElement.setCreatorPlayground(userPlayground);
		//expectedElement.setLocation(new Location(10, 10));
		expectedElement.setX(10.0);
		expectedElement.setY(10.0);
		expectedElement.setName("Rex");
		expectedElement.setType("Dog");
		Map<String, Object> attributesForexpectedElement = new HashMap<String, Object>();
		attributesForexpectedElement.put("Play", "Woof");
		expectedElement.setAttributes(attributesForexpectedElement);
		expectedElement.setCreationDate(null);

		assertThat(actualElement).isNotNull().isEqualTo(expectedElement);

	}

	// A
	@Test(expected = Exception.class)
	public void testUpdateNonExistingElement() throws Exception {
		String userEmail = "Manger@mail.com";
		String userPlayground = "2019a.talin"; 
		
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/2019a.talin/{ID}";
		String ID = "abc";

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

		this.restTemplate.put(url, updatedElementTO,userPlayground,userEmail, ID);

		// Then the response status <> 2xx
	}
	
	// A
	@Test(expected = Exception.class)
	public void testUpdateAnElementWithPlayerAccount() throws Exception {
		// Given Server is up
		String userEmail = "Player@mail.com";
		String MangerEmail = "Manger@mail.com";
		String userPlayground = "2019a.talin"; 
		String url = base_url + "/playground/elements/{userPlayground}/{userEmail}/{Playground}/{Id}";

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("Play", "Woof");

		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setCreatorEmail(MangerEmail);
		elementEntity.setCreatorPlayground(userPlayground);
		elementEntity.setCreationDate(null);
		elementEntity.setExirationDate(null);
		
		//create a manger to add element
		createMangerAccount(MangerEmail, userPlayground);
		
		//And the database contains an Element

		elementEntity = this.elementService.addNewElement(elementEntity.getCreatorPlayground(),elementEntity.getCreatorEmail(),elementEntity);
		
		
		//database contains an player
		createPlayerAccount(userEmail, userPlayground);
		
		
		//When I Put http://localhost:8083/playground/elements/2019a.talin/Manger@mail.com/2019a.talin/ElementID
		
		ElementTO updatedElementTO = new ElementTO();
		updatedElementTO.setId(elementEntity.getId());
		updatedElementTO.setPlayground(elementEntity.getPlayground());
		updatedElementTO.setLocation(new Location(10, 10));
		updatedElementTO.setName("Rex");
		updatedElementTO.setType("Dog");
		updatedElementTO.setAttributes(attributes);
		updatedElementTO.setCreationDate(null);
		

		this.restTemplate.put(url, updatedElementTO,userPlayground,userEmail,elementEntity.getPlayground(),elementEntity.getId());

		//Then the response status <> 2XX
		
	}

	
	// I
	@Test
	public void TestElementCreatedWithManagerAccountSuccessfully() throws Exception {
		// Given Server is up

		String url = base_url + "/playground/elements/{userPlayground}/{email}";
		String email = "Manger@mail.com";
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
		//database contains an manger:
		createMangerAccount(email, playground);
		
		// When I POST
		// http://localhost:8083/playground/elements/2019a.talin/Manger@mail.com
		// And with body
		// {
		// �name�: �cat�,
		// �type�:�animal�
		// �location�:{�x�:1.0,�y�:1.0},
		// "attributes": {}
		// }
		// with headers:
		// Accept: application/json
		// Content-Type: application/json
		ElementTO elementAfterPost = this.restTemplate.postForObject(url, newElement, ElementTO.class, playground, email);
		// Then the response status is 2xx
		// and the database contains for playground+id:�2019a.talin"+x
		// {
		// �playground�: 2019a.talin�
		// � id�: x
		// "x": 1,
		// "y": 1
		// "name": "Animal", "type": "Animal", "attributes": {},
		// "creatorPlayground":"2019a.talin", "creatorEmail":"Manger@mail.com"
		// }
		ElementEntity elementEntityExist = this.elementService.getElement(elementAfterPost.getCreatorPlayground(),elementAfterPost.getCreatorEmail(),elementAfterPost.getId(), playground);

		assertThat(elementEntityExist).extracting("name", "type", "x","y", "attributes").containsExactly(name, type,
				x,y, attributes);
	}
	
	// I
	@Test(expected = Exception.class)
	public void TestElementCreatedWithPlayerAccount() throws Exception {
		// Given Server is up

		String url = base_url + "/playground/elements/{userPlayground}/{email}";
		String email = "Player@mail.com";
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
		//database contains an player:
		createPlayerAccount(email, playground);
		
		// When I POST
		// http://localhost:8083/playground/elements/2019a.talin/Player@mail.com
		// And with body
		// {
		// �name�: �cat�,
		// �type�:�animal�
		// �location�:{�x�:1.0,�y�:1.0},
		// "attributes": {}
		// }
		// with headers:
		// Accept: application/json
		// Content-Type: application/json
		this.restTemplate.postForObject(url, newElement, ElementTO.class, playground, email);
		// Then the response status is <>2xx
	}

	// I
	@Test
	public void TestGetElementSuccessfully() throws Exception {

		// Given Server is up
		// And the database contains
		// {
		// �playground�: 2019a.talin�
		// � id�: x
		// "x": 1,
		// "y": 1
		// "name": "cat",
		// "type": "Animal",
		// "attributes": {},
		// "creatorPlayground":"2019a.talin",
		// "creatorEmail":�R@mail.com"
		// }

		
		String url = base_url + "/playground/elements/{userPlayground}/{email}/{playground}/{id}";
		String id ;
		String email = "R@mail.com";
		String name = "cat";
		double x = 1.0;
		double y = 1.0;
		String type = "animal";
		//database contains an manager:
		createMangerAccount(email, playground);
		
		ElementEntity newElement = new ElementEntity();
		newElement.setName(name);
		//newElement.setLocation(new Location(x, y));
		newElement.setX(x);
		newElement.setY(y);
		newElement.setType(type);
		newElement.setCreatorEmail(email);

		newElement = this.elementService.addNewElement(newElement.getPlayground(),newElement.getCreatorEmail(),newElement);

		
		id = newElement.getId();
		// When I Get
		// http://localhost:8083/playground/elements/2019a.talin/R@mail.com/2019a.talin/x
		// with headers:
		// Accept: application/json

		ElementTO actualElement = this.restTemplate.getForObject(url, ElementTO.class, playground, email, playground,
				id);

		// Then the response status is 2xx and body is

		// {
		// �playground�: 2019a.talin�
		// � id�: x
		// "location": {
		// "x": 1,
		// "y": 1
		// },
		// "name": "Animal", "type": "Animal", "attributes": {},
		// "creatorPlayground":"2019a.talin", "creatorEmail":"R@mail.com"
		// }
		assertThat(actualElement).isNotNull().extracting("playground", "id", "name", "type", "location", "attributes")
				.containsExactly(playground, id, name, type, new Location(x, y), new HashMap<>());
	}

	// I
	@Test(expected = Exception.class)
	public void TestGetElementWithInvalidId() throws Exception {
		// Given Server is up
		String url = base_url + "/playground/elements/{userPlayground}/{email}/{playground}/{id}";
		String invalidId = "123";
		String email = "R@mail.com";
		
		//database contains an manager:
		createMangerAccount(email, playground);
		
		// When I GET
		// http://localhost:8083/playground/elements/2019a.talin/R@mail.com/2019a.talin/123
		// with headers:
		// Accept: application/json
		this.restTemplate.getForObject(url, ElementTO.class, playground, email, playground,
				invalidId);
		// Then the response status is <> 2xx
	}
	
	// I
	@Test(expected = Exception.class)
	public void TestGetElementWithInvalidUser() throws Exception {
		//		Given Server is up
		//        And the database contains 
		//{
		//�playground�:  2019a.talin�
		//� id�: x
		//"x":  1,
		//"y": 1
		//"name": "cat",
		//"type": "Animal",
		//"attributes": {},
		//"creatorPlayground":"2019a.talin",
		//"creatorEmail":�Talin@email.com"
		//}
		//
		String url = base_url + "/playground/elements/{userPlayground}/{email}/{playground}/{id}";
		String email = "Tali@mail.com";
		String invalidEmail = "try@mail.com";

		String name = "cat";
		double x = 1.0;
		double y = 1.0;
		String type = "animal";
		
		//database contains an manager:
		createMangerAccount(email, playground);
		
		ElementEntity newElement = new ElementEntity();
		newElement.setName(name);
		//newElement.setLocation(new Location(x, y));
		newElement.setX(x);
		newElement.setY(y);
		newElement.setType(type);
		newElement.setCreatorEmail(email);

		newElement = this.elementService.addNewElement(newElement.getPlayground(),newElement.getCreatorEmail(),newElement);
		
		//When I GET http://localhost:8083/playground/elements/2019a.talin/try@mail.com/2019a.talin/x
		//with headers:
		//Accept: application/json
		
		this.restTemplate.getForObject(url, ElementTO.class, playground, invalidEmail, playground,
				newElement.getId());
		// Then the response status is <> 2xx
	}

	// T
	@Test
	public void testGetEementsWithThisAttributeValueUsingDefaultPagination() throws Exception {

		int DefaultSize = 10;
		String url = base_url + "/playground/elements/{userPlayground}/{email}/search/name/cat";
		String email = "tali@mail.com";

		/*
		 * Given Server is up And the database contains 20 elements with the name cat
		 */

		setElementsDatabase(20);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class, playground, email);

		// then
		assertThat(actualElement).isNotNull().hasSize(DefaultSize);
	}

	// T
	@Test
	public void testGetElementsWithAttributeValueThatNotExistsUsingDefaultPagination() throws Exception {

		String url = base_url + "/playground/elements/{userPlayground}/{email}/search/name/cat";
		String email = "tali@mail.com";
		/*
		 * Given Server is up
		 */

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class, playground, email);

		// then
		assertThat(actualElement).isEmpty();
	}

	// T
	@Test(expected = Exception.class)
	public void testGetElementsWithInvalidAttributeUsingDefaultPagination() throws Exception {
		int DefaultSize = 10;
		String email = "tali@mail.com";
		String url = base_url + "/playground/elements/{userPlayground}/{email}/search/Momo/cat";

		/*
		 * Given Server is up
		 */

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class, playground, email);

		// then
		assertThat(actualElement).hasSize(DefaultSize);
	}

	// T
	@Test
	public void testGetElementsWithThisAttributeValueUsingPaginationSuccessfully() throws Exception {

		int size = 3;
		String email = "tali@mail.com";
		String url = base_url + "/playground/elements/{userPlayground}/{email}/search/name/cat" + "?size=" + size;

		/*
		 * Given Server is up And the database contains 10 elements with the name cat
		 */
		setElementsDatabase(10);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class, playground, email);

		// then
		assertThat(actualElement).isNotNull().hasSize(size);
	}

	// T
	@Test
	public void testGetElementsWithThisAttributeValueUsingPaginationOf100PageSuccessfully() throws Exception {

		int size = 6;
		int page = 100;
		String email = "tali@mail.com";
		String url = base_url + "/playground/elements/{userPlayground}/{email}/search/name/cat" + "?size=" + size
				+ "&page=" + page;

		/*
		 * Given Server is up And the database contains 10 elements with the name cat
		 */
		setElementsDatabase(10);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class, playground, email);

		// then
		assertThat(actualElement).isEmpty();
	}

	// T
	@Test
	public void testGetElementsWithThisAttributeValueUsingPaginationOfSecondPageSuccessfully() throws Exception {

		int size = 6;
		int page = 1;
		int numOfElements = 10;
		String email = "tali@mail.com";
		String url = base_url + "/playground/elements/{userPlayground}/{email}/search/name/cat" + "?size=" + size
				+ "&page=" + page;

		/*
		 * Given Server is up And the database contains 10 elements with the name cat
		 */
		setElementsDatabase(10);

		// when
		ElementTO[] actualElement = this.restTemplate.getForObject(url, ElementTO[].class, playground, email);

		// then
		assertThat(actualElement).isNotNull().hasSize(numOfElements - size);
	}

	// T
	@Test(expected = Exception.class)
	public void testGetElementsWithThisAttributeValueWithInvalidPageSize() {
		String email = "tali@mail.com";
		// when
		String url = base_url + "/playground/elements/{userPlayground}/{email}/search/name/cat";

		/*
		 * Given Server is up
		 */

		// When
		this.restTemplate.getForObject(url + "?size={size}&page={page}", ElementTO[].class,
				playground, email, -6, 1);

		// Then the response status is <> 2xx
	}
}
