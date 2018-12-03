package playground.layout.WebUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import playground.layout.ActivityTO;
import playground.layout.ErrorMessage;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.Exceptions.UserNotFoundException;
import playground.logic.Services.PlaygroundActivityService;

@RestController
public class WebUIActivity {
	
	private PlaygroundActivityService activityService;
	
	public PlaygroundActivityService getActivityService() {
		return activityService;
	}

	@Autowired
	public void setActivityService(PlaygroundActivityService activityService) {
		this.activityService = activityService;
	}


	private void validateNull(String name) throws Exception {
		if ("null".equals(name) || name == null) {
			throw new Exception("user not found");
		}
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
		
		boolean activityResult =  activityService.validateActivityType(activityTo.getType());
		
		if(activityResult) {
			//update attributes that come from url
			activityTo.setPlayerEmail(email);
			activityTo.setPlayerPlayground(userPlayground);
			ActivityEntity activityEntity = activityTo.convertFromActivityTOToActivityEntity();
			return new ActivityTO(
					this.activityService.addNewActivity(activityEntity));
		}
		
		else throw new RuntimeException("Invalid Activity Type");
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









