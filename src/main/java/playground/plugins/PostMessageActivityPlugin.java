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
public class PostMessageActivityPlugin implements PlaygroungActivityPlugin {
	
	private UserDao users;
	private ElementDao elements;
	
	@Autowired
	public PostMessageActivityPlugin(UserDao users,ElementDao elements) {
		this.users = users;
		this.elements = elements;
	} 
	
	@Override
	public ElementEntity checkAction(ActivityEntity activity) {
		String element_key = activity.getElementPlayground() + "@@" +activity.getElementId();
		ElementEntity element = this.elements.findById(element_key)
		.orElseThrow(()->new ElementNotFoundException("no Element for: " + element_key));
		
		if(!"Board".equalsIgnoreCase(element.getType()))
			throw new RuntimeException("Not a Board!");
		
		return element;
	}
	
	@Override
	public Object invokeAction(ActivityEntity activity, String activityId, ElementEntity element) {
		String UserKey = activity.getPlayerPlayground() +"@@"+activity.getPlayerEmail(); 
		
		// user Exist check
		UserEntity user = this.users.findById(UserKey)
		.orElseThrow(()->new UserNotFoundException("no user found for: " + UserKey));
		
		// user verified check
//		if(!user.isVerified())
//			throw new RuntimeException("The user " + UserKey+ " is not verified.");
		
		return new Message(activityId, "the user " + user.getUsername() +" posted a message in " + element.getName());
	
	}

}
