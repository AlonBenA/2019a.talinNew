package playground.layout;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundUserService;

@Component
public class testHelper {
	

	private PlaygroundUserService userService;
	String userPlayground = "2019a.talin";
	
	@Autowired
	public testHelper(PlaygroundUserService userService) {
		this.userService = userService;
	}
	
	public void AddNewUser(String userEmail,String role,Boolean toValidate)
	{
		String username = "user1";
		String avatar = "https://goo.gl/images/WqDt96";
		UserEntity user = new UserEntity(userEmail, username, avatar, role);
		user.setPlayground(userPlayground);
		
		//add the user to the database
		user = userService.addNewUser(user);
		
		if(toValidate)
		{
			//validate user to the database
			userService.validateUser(user, user.getCode());
		}

	}
	
	public void teardown() {
		// cleanup databases
		this.userService.cleanup();
	}
	
	

}
