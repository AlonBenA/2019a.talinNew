package playground.plugins;

import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;

public interface PlaygroungActivityPlugin {
	public Object invokeAction(ActivityEntity activity,String activityId,ElementEntity element);
	
	public ElementEntity checkAction(ActivityEntity activity);
}
