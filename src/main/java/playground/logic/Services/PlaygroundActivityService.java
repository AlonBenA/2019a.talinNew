package playground.logic.Services;

import java.util.List;

import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.Exceptions.UserNotFoundException;

public interface PlaygroundActivityService {
	
	public ActivityEntity addNewActivity(ActivityEntity activityEntity);
	
	public ActivityEntity getActivity(String activity_key) throws Exception;
	
	public void cleanup();
	
	public boolean validateActivityType(String type);

}
