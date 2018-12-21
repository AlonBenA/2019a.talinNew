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
public class PostMessageAcitivtyPlugin implements PlaygroungActivityPlugin {
	
	private UserDao users;
	private ElementDao elements;
	
	@Autowired
	public PostMessageAcitivtyPlugin(UserDao users,ElementDao elements) {
		this.users = users;
		this.elements = elements;
	} 
	
	@Override
	public Object invokeAction(ActivityEntity activity) {
		String UserKey = activity.getPlayerPlayground() +"@@"+activity.getPlayerEmail(); 
		
		// user Exist check
		UserEntity user = this.users.findById(UserKey)
		.orElseThrow(()->new UserNotFoundException("no user found for: " + UserKey));
		
		// user verified check
		if(!user.isVerified())
			throw new RuntimeException("The user " + UserKey+ " is not verified.");
		
		String element_key = activity.getElementPlayground() + "@@" +activity.getElementId();
		ElementEntity element = this.elements.findById(element_key)
		.orElseThrow(()->new ElementNotFoundException("no Element for: " + element_key));
		
		
		if("Board".equalsIgnoreCase(element.getType()))
			return new Message("the user " + UserKey +" posted a message in " + element_key + " Board");
		
		throw new RuntimeException("Cannot post a message in non Board element!");
		
	}

}
