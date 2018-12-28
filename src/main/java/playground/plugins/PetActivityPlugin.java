package playground.plugins;

import java.util.Date;

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
public class PetActivityPlugin implements PlaygroungActivityPlugin {
	private UserDao users;
	private ElementDao elements;

	@Autowired
	public PetActivityPlugin(UserDao users, ElementDao elements) {
		this.users = users;
		this.elements = elements;
	}

	@Override
	public Object invokeAction(ActivityEntity activity, String activityId, ElementEntity element) {
		String UserKey = activity.getPlayerPlayground() +"@@"+activity.getPlayerEmail(); 
		UserEntity user = this.users.findById(UserKey)
		.orElseThrow(()->new UserNotFoundException("no user found for: " + UserKey));
		
		Long numberOfPointsForPet = (long) 10;
		String numberOfPet = "numberOfPet";
		String DateOfPet = "DateOfPet"; 
			
		if(element.getAttributes().containsKey(numberOfPet) && element.getAttributes().containsKey(DateOfPet))
		{
			Date date = (Date) element.getAttributes().get(DateOfPet);
			int numberOfPetInt = (int) element.getAttributes().get(numberOfPet);
			if(DateUtils.isToday(date) && numberOfPetInt > 20)
			{
				
					Message message = new Message();
					message.setId(activityId);					
					message.setMessage("You can't pet that" + element.getName() + " anymore \n");
					return message;
			}
			else
			{
				//Add 1 to number of pet times
				element.getAttributes().put(numberOfPet,numberOfPetInt+1);
				element.getAttributes().put(DateOfPet,new Date());		
				elements.save(element);			
			}
		}else {
			//create number of pet times and date
			int numberOfPetInt = 1;
			element.getAttributes().put(numberOfPet,numberOfPetInt);
			element.getAttributes().put(DateOfPet,new Date());
			elements.save(element);
		}
			//Add point to user and save the activity
			user.increasePoints(numberOfPointsForPet);
			users.save(user);			
			Message message = new Message();
			message.setId(activityId);
			message.setMessage(numberOfPointsForPet+ " point to" + user.getUsername() + " for pet " + element.getName());
		
			return message;

	}

	@Override
	public ElementEntity checkAction(ActivityEntity activity) {
		// Set key for element
		String element_key = activity.getElementPlayground() + "@@" + activity.getElementId();
		ElementEntity element = this.elements.findById(element_key)
				.orElseThrow(() -> new ElementNotFoundException("no Element for: " + element_key));

		if (!"Animal".equalsIgnoreCase(element.getType())) {
			throw new RuntimeException("Not an Animal!");
		}

		return element;
	}

}
