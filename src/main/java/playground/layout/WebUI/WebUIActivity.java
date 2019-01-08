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
import playground.logic.Entities.ActivityEntity;
import playground.logic.Services.PlaygroundActivityService;
import playground.logic.jpa.ElementNotFoundException;
import playground.logic.jpa.UserNotFoundException;

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


	
	//Sprint2: Write the /playground/activities/{userPlayground}/{email} 
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/activities/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public Object activateElement (
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @RequestBody ActivityTO activityTo) throws Exception {
		
		//boolean activityResult =  activityService.validateActivityType(activityTo.getType());
		
		//update attributes that come from url
		activityTo.setPlayerEmail(email);
		activityTo.setPlayerPlayground(userPlayground);
		ActivityEntity activityEntity = activityTo.convertFromActivityTOToActivityEntity();
		return this.activityService.addNewActivity(userPlayground, email, activityEntity);
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









