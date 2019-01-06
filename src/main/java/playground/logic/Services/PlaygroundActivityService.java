package playground.logic.Services;
import playground.logic.Entities.ActivityEntity;

public interface PlaygroundActivityService {
	
	public Object addNewActivity(String userPlayground, String email, ActivityEntity activityEntity);
	
	public ActivityEntity getActivity(String userPlayground, String email, String activity_id, String playground) throws Exception;
	
	public void cleanup();
	
	public boolean validateActivityType(String type);

}
