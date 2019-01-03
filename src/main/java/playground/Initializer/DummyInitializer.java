package playground.Initializer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import playground.logic.Location;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundElementService;
import playground.logic.Services.PlaygroundUserService;
import playground.logic.jpa.ElementAlreadyExistException;

@Component
@Profile("demo")
public class DummyInitializer {

	
	private PlaygroundElementService elementService;
	private PlaygroundUserService userService;
	private Log log;
	String playground;
	
	@Autowired
	public DummyInitializer(PlaygroundElementService elementService,PlaygroundUserService userService) {
		super();
		this.elementService = elementService;
		this.userService = userService;
	}
	
	
	@Value("${playground}") // set playground as "2019a.talin"
	private void setPlayground(String playground) {
		this.playground = playground;
	}
	
	@PostConstruct
	public void init () {
		this.log = LogFactory.getLog(DummyInitializer.class);
		String demouserEmail = "demouser@mail.com";
		String demousername = "Gryffindor";
		String avatar = "https://goo.gl/images/WqDt96";
		String role = "Player";
		
		
		//create demo user
		createDemoUsers( demouserEmail, demousername , avatar , role);
		
		
		//create demo manager
		 demouserEmail = "demoManager@mail.com";
		 demousername = "demoManager";
		 avatar = "https://goo.gl/images/WqDt96";
		 role = "Manager";
		 
		 createDemoUsers( demouserEmail, demousername , avatar , role);
		 
		 
		 createDemoElements(demouserEmail);
		
	}
	
	
	private void createDemoUsers(String userEmail,String username ,String avatar ,String role)
	{

		UserEntity user = new UserEntity(userEmail, username, avatar, role);
		
		//add the user to the database
		user = userService.addNewUser(user);
		userService.validateUser(user, user.getCode());
	}
	
	
	private void createDemoElements(String creatorEmail)
	{
		int numberOFElements = 10;
		String name = "Tom the cat";
		
		Date exirationDate = null;
		String type = "animal";
		Map<String, Object> attributes = new HashMap<>();


		// location,value,exirationDate,type,attributes,creatorPlayground,creatorEmail
		IntStream.range(0, numberOFElements) // int stream
				.mapToObj(value -> new ElementEntity(new Location(value, value),
						(name == null) ? "animal #" + value : name, exirationDate, type, attributes, playground,
						creatorEmail)) // ElementTO stream using constructor
										// reference
				.forEach(t -> {
					try {
						elementService.addNewElement(playground, creatorEmail, t);
					} catch (ElementAlreadyExistException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
		
		
		Date eDate = new Date();
	
		
		ElementEntity elementWithExirationDate = new ElementEntity(new Location(11, 11),
				"animal #" + 12, eDate, type, attributes, playground,
				creatorEmail);
		
		ElementEntity Board = new ElementEntity(new Location(11, 11),
				"board #" + 13 , null, "Board", attributes, playground,
				creatorEmail);
		
		try {
			elementService.addNewElement(playground, creatorEmail, elementWithExirationDate);
			
			elementService.addNewElement(playground, creatorEmail, Board);
			
		} catch (ElementAlreadyExistException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

	}
	
	
	
	
	

}
