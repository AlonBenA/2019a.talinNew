package playground.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.jpadal.ElementDao;
import playground.jpadal.UserDao;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.jpa.ElementNotFoundException;
import playground.logic.jpa.UserNotFoundException;

@Component
public class FeedAcitivtyPlugin implements PlaygroungActivityPlugin {
	private UserDao users;
	private ElementDao elements;
	
	@Autowired
	public FeedAcitivtyPlugin(UserDao users,ElementDao elements) {
		this.users = users;
		this.elements = elements;
	} 

	@Override
	public Object invokeAction(ActivityEntity activity) {
		String UserKey = activity.getPlayerPlayground() +"@@"+activity.getPlayerEmail(); 
		UserEntity user = this.users.findById(UserKey)
		.orElseThrow(()->new UserNotFoundException("no user found for: " + UserKey));
		
		// set key for element 
		String element_key = activity.getElementPlayground() + "@@" +activity.getElementId();
		ElementEntity element = this.elements.findById(element_key)
		.orElseThrow(()->new ElementNotFoundException("no Element for: " + element_key));
		
		if("Animal".equalsIgnoreCase(element.getType()))
		{
			//Add point to user
		}
		else
		{
			throw new RuntimeException("Not an Animal!");
		}
		
	
		
		return activity;
	}
	
	

}
