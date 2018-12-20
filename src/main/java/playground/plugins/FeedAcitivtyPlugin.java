package playground.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import playground.jpadal.UserDao;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.jpa.UserNotFoundException;

@Component
public class FeedAcitivtyPlugin implements PlaygroungActivityPlugin {
	private UserDao users;
	
	@Autowired
	public FeedAcitivtyPlugin(UserDao users) {
		this.users = users;
	}

	@Override
	public Object invokeAction(ActivityEntity activity) {
		String key = activity.getPlayerPlayground() +"@@"+activity.getPlayerEmail(); 
		UserEntity user = this.users.findById(key)
		.orElseThrow(()->new UserNotFoundException("no user found for: " + key));
		
		//Add point to user
		
		return activity;
	}
	
	

}
