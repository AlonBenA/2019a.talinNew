package playground.plugins;

import java.util.Date;

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
		
		int numberOfPointsForFeed = 1;
		String numberOfFeed = "numberOfFeed";
		String DateOfFeed = "DateOfFeed"; 
			
		if(element.getAttributes().containsKey(numberOfFeed) && element.getAttributes().containsKey(DateOfFeed))
		{
			Date date = (Date) element.getAttributes().get(DateOfFeed);
			int numberOfFeedInt = (int) element.getAttributes().get(numberOfFeed);
			if(DateUtils.isToday(date) && numberOfFeedInt > 3)
			{
				
					Message message = new Message();
					message.setId(activityId);					
					message.setMessage("You can't feed that" + element.getName() + " anymore \n");
					return message;
			}
			else
			{
				//Add point to user and save the activity
				user.increasePoints(new Long(1));
				users.save(user);
			
				//Add 1 to number of feed times
				element.getAttributes().put(numberOfFeed,numberOfFeedInt+1);
				element.getAttributes().put(DateOfFeed,new Date());		
				elements.save(element);
				
				Message message = new Message();
				message.setId(activityId);
				message.setMessage(numberOfPointsForFeed+ " point to" + user.getUsername() + " for feed " + element.getName());
			
				return message;
			
			}
		}

			//Add point to user and save the activity
			user.increasePoints(new Long(1));
			users.save(user);
		
			//create number of feed times and date
			int numberOfFeedInt = 1;
			element.getAttributes().put(numberOfFeed,numberOfFeedInt);
			element.getAttributes().put(DateOfFeed,new Date());
			elements.save(element);
			
			Message message = new Message();
			message.setId(activityId);
			message.setMessage(numberOfPointsForFeed+ " point to" + user.getUsername() + " for feed " + element.getName());
		
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
