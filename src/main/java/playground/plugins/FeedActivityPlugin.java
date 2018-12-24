package playground.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.jpadal.ActivityDao;
import playground.jpadal.ElementDao;
import playground.jpadal.UserDao;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.jpa.ElementNotFoundException;
import playground.logic.jpa.UserNotFoundException;

@Component
public class FeedActivityPlugin implements PlaygroungActivityPlugin {
	private UserDao users;
	private ElementDao elements;
	
	@Autowired
	public FeedActivityPlugin(UserDao users,ElementDao elements) {
		this.users = users;
		this.elements = elements;
	} 

	@Override
	public Object invokeAction(ActivityEntity activity,String activityId,ElementEntity element) {
		
		String UserKey = activity.getPlayerPlayground() +"@@"+activity.getPlayerEmail(); 
		UserEntity user = this.users.findById(UserKey)
		.orElseThrow(()->new UserNotFoundException("no user found for: " + UserKey));
				

			//Add point to user and save the activity
			user.increasePoints(new Long(1));
			users.save(user);
			Message message = new Message();
			message.setId(activityId);
			message.setMessage("the user " + user.getUsername() +" feed "+ element.getName());
			return message;
	
	}

	@Override
	public ElementEntity checkAction(ActivityEntity activity) {
		
		// Set key for element 
				String element_key = activity.getElementPlayground() + "@@" +activity.getElementId();
				ElementEntity element = this.elements.findById(element_key)
				.orElseThrow(()->new ElementNotFoundException("no Element for: " + element_key));
				
				if(!"Animal".equalsIgnoreCase(element.getType()))
				{
					throw new RuntimeException("Not an Animal!");
				}
		
				return element;
	}
	
	

}
