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
		String UserKey = activity.getPlayerPlayground() + "@@" + activity.getPlayerEmail();
		UserEntity user = this.users.findById(UserKey)
				.orElseThrow(() -> new UserNotFoundException("no user found for: " + UserKey));

//		// user verified check
//		if (!user.isVerified())
//			throw new RuntimeException("The user " + UserKey + " is not verified.");
//		// user player check
//		if (!"Player".equalsIgnoreCase(user.getRole()))
//			throw new RuntimeException("The user " + UserKey + " is not player.");
		// Add point to user and save the activity
		user.increasePoints(new Long(10));
		users.save(user);

		return new Message(activityId, "the user " + user.getUsername() + " pet " + element.getName());
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
