package playground.layout.WebUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import playground.layout.ErrorMessage;
import playground.layout.NewUserForm;
import playground.layout.UserTO;
import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundUserService;
import playground.logic.jpa.ElementNotFoundException;
import playground.logic.jpa.JpaUserService;
import playground.logic.jpa.UserNotFoundException;

@RestController
public class WebUIUser {
	
	private PlaygroundUserService userService;
	
	@Autowired
	public void setPlaygroundService(PlaygroundUserService userService) {
		this.userService = userService;
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
		userService.updateUser(updatedUser.convertFromUserTOToUserEntity(), email, playground);

	}
	
	// Rest api 1 - Sapir 
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/users",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public UserTO userSignup (@RequestBody NewUserForm newUserForm) {
		UserEntity userEntity = userService.addNewUser(
				new UserEntity(newUserForm.getEmail(),newUserForm.getUsername(),
						newUserForm.getAvatar(),newUserForm.getRole()));

		System.err.println("user: " + userEntity.getPlayground() + "@@" + userEntity.getEmail() + " code: "
				+ userEntity.getCode()); // "send" code
		return new UserTO(userEntity);
	}
	
	// Rest api 2 - Sapir
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/confirm/{playground}/{email}/{code}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO userValidate (@PathVariable("playground") String userPlayground,@PathVariable("email") String email, @PathVariable("code") String code) throws Exception {
		UserEntity not_Verified_UserEntity = userService.getUser(email, userPlayground);
		UserEntity verified_UserEntity = userService.validateUser(not_Verified_UserEntity, code);
		return new UserTO(verified_UserEntity);
	}
	
	// Rest api 3 - Sapir
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/login/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO login (@PathVariable("playground") String playground,@PathVariable("email") String email) throws Exception {
		UserEntity userEntity = userService.getUser(email, playground);
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









