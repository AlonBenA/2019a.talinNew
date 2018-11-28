package playground.layout;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.Location;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.Exceptions.UserNotFoundException;
import playground.logic.Services.PlaygroundService;

@RestController
public class WebUI {
	
	private PlaygroundService playgroundService;
	////////////////////////////////////////////////////////
	private String defaultUserName; // remove/comment
	///////////////////////////////////////////////////////
	

	public PlaygroundService getPlaygroundService() {
		return playgroundService;
	}

	@Autowired
	public void setPlaygroundService(PlaygroundService playgroundService) {
		this.playgroundService = playgroundService;
	}

	////////////////////////////////////////////////////////
	@Value("${name.of.user.to.be.greeted:Anonymous}")			// remove/comment
	public void setDefaultUserName(String defaultUserName) {	// remove/comment
		this.defaultUserName = defaultUserName;					// remove/comment
	}															// remove/comment
	///////////////////////////////////////////////////////
	
	private void validateNull(String name) throws Exception {
		if ("null".equals(name) || name == null) {
			throw new Exception("user not found");
		}
	}

	//Sprint2: Write the GET/playground/elements/{userPlayground}/{email}/search/{attributeNa me}/{value} 
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsWithAttribute(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("attributeName") String attributeName,
			@PathVariable("value") String value,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		
		//validate user
		validateNull(email);
		validateNull(userPlayground);
		
		// validate attribute
		boolean attributeNameResult =  playgroundService.validateElementAttribteName(attributeName);
		
		if(attributeNameResult) {
			List<ElementTO> elementsWithAttribute = 
					playgroundService.getAllElements(size, page) // list of entities
					.stream() // stream of entities
					.filter(element -> (attributeName.equals("name") ? element.getName().equals(value)
							: element.getType().equals(value)))
					.map(ElementTO::new) // stream of boundaries
					.collect(Collectors.toList());// list of boundaries
				
			return elementsWithAttribute.toArray(new ElementTO[0]);
		}
		
		else throw new RuntimeException("Invalid Attribute for searching elements");
	}
	
	//Sprint2: Write the /playground/activities/{userPlayground}/{email} 
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/activities/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public Object activateElement (
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @RequestBody ActivityTO activityTo) throws Exception {
		
		//validate user
		validateNull(email);
		validateNull(userPlayground);
		
		boolean activityResult =  playgroundService.validateActivityType(activityTo.getType());
		
		if(activityResult) {
			//update attributes that come from url
			activityTo.setPlayerEmail(email);
			activityTo.setPlayerPlayground(userPlayground);
			ActivityEntity activityEntity = activityTo.convertFromActivityTOToActivityEntity();
			ActivityEntity ac= playgroundService.addNewActivity(activityEntity);
			return ac;
		}
		
		else throw new RuntimeException("Invalid Activity Type");
	}
	
	//Sprint2: Write the GET /playground/elements/{userPlayground}/{email}/all
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/all",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements (@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
			
		try {		
				return playgroundService.getAllElements(size, page) // list of entities
				.stream() // stream of entities
				.map(ElementTO::new) // stream of boundaries
				.collect(Collectors.toList())// list of boundaries
				.toArray(new ElementTO[0]); // ElementTO[]
			
		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving data");
		}		
	}
	
	
	//Sprint2: Write the GET /playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distan ce}
	// to check {distan ce} Represents 
	@RequestMapping(
			method=RequestMethod.GET,
			path= "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distan ce}" ,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllNearElements (@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("x") double x,
			@PathVariable("y") double y,
			@PathVariable("distan ce") double center,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		
			return playgroundService.getAllNearElements(x, y, center, size, page)
					.stream()
					.map(ElementTO::new)
					.collect(Collectors.toList())
					.toArray(new ElementTO[0]);


			//throw new RuntimeException("Error while retrieving data");
			
			
	}
	
	
	public List<ElementTO> getListOfElementsTO()
	{
		Location location1 = new Location(10.0,10.0);
		Location location2 = new Location(100.0,100.0);
		String name1 = "Dog";
		String name2 = "Cat";
		String type1 = "Dog";
		String type2 = "Cat";
		Map<String,Object> attributes1  = new HashMap<>();
		attributes1.put("Play", "Woof");
		Map<String,Object> attributes2 = new HashMap<>();
		attributes2.put("Play", "Meow");
		String creatorPlayground = "2019a.Talin";
		String creatorEmail = "AlonMail@Mail.com";
		
		List<ElementTO> elements = Arrays.asList(
				new ElementTO(location1,name1, new Date(),type1,attributes1,creatorPlayground,creatorEmail),
				new ElementTO(location2,name2, new Date(),type2,attributes2,creatorPlayground,creatorEmail),
				new ElementTO()
				);
		
		return elements;
	}
	
	//Sprint2: Write the PUT /playground/elements/{userPlayground}/{email}/{playground}/{id}
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateElement (
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("playground") String playground,
			@PathVariable("id") String id,
			@RequestBody ElementTO updatedElement) throws Exception {
		
		playgroundService.updateElement(updatedElement.convertFromElementTOToElementEntity(), playground, id);
		
	}
	
	//Sprint2: Write the PUT /playground/users/{playground}/{email}
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/users/{playground}/{email}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateUser (
			@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@RequestBody UserTO updatedUser) throws Exception {
		//set to elementTo the userPlayground and Email from URL
		updatedUser.setEmail(email);
		updatedUser.setPlayground(playground);
		playgroundService.updateUser(updatedUser.convertFromUserTOToUserEntity(), email, playground);

	}
	
	//Sprint2: Write the POST /playground/elements/{userPlayground }/{email}
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/elements/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO createElement (
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@RequestBody ElementTO elementTO) throws Exception {
		
		//set to elementTo the CreatorPlayground and CreatorEmail from URL
		elementTO.setCreatorEmail(email);
		elementTO.setCreatorPlayground(userPlayground);
		
		return new ElementTO(
				this.playgroundService.addNewElement(elementTO.convertFromElementTOToElementEntity()));
	}
	
	
	//Sprint2: Write the GET /playground/elements/{userPlayground}/{email}/{playground}/{id}
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElement (
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) throws ElementNotFoundException {
		return new ElementTO(this.playgroundService.getElement(id, playground));	
	}
	
	// Rest api 1 - Sapir 
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/users",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public UserTO userSignup (@RequestBody NewUserForm newUserForm) {
		UserEntity userEntity = playgroundService.addNewUser(
				new UserEntity(newUserForm.getEmail(),newUserForm.getUsername(),
						newUserForm.getAvatar(),newUserForm.getRole()));
		
		//userEntity.getCode(); // send code???????????????
		return new UserTO(userEntity);
	}
	
	// Rest api 2 - Sapir
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/confirm/{playground}/{email}/{code}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO userValidate (@PathVariable("playground") String userPlayground,@PathVariable("email") String email, @PathVariable("code") String code) throws Exception {
		UserEntity userEntity = playgroundService.getUser(email, userPlayground);
		userEntity.verify(code);
		if(!userEntity.isVerified())
			throw new RuntimeException("Wrong code");
		return new UserTO(userEntity);
	}
	
	// Rest api 3 - Sapir
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/login/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO login (@PathVariable("playground") String playground,@PathVariable("email") String email) throws Exception {
		UserEntity userEntity = playgroundService.getUser(email, playground);
		boolean flag = userEntity.isVerified();
		if(!flag)
			throw new RuntimeException("User not verified");
		return new UserTO(userEntity);
	}
	
	@ExceptionHandler//(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException (UserNotFoundException e) {
		String errorMessage = e.getMessage();
		if (errorMessage == null) {
			errorMessage = "There is no relevant message";
		}
		return new ErrorMessage(errorMessage);
	}	
	
	@ExceptionHandler//(ElementNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException (ElementNotFoundException e) {
		String errorMessage = e.getMessage();
		if (errorMessage == null) {
			errorMessage = "There is no relevant message";
		}
		return new ErrorMessage(errorMessage);
	}
	

		

	

	
	
}









