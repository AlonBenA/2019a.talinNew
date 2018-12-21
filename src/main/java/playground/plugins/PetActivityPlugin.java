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
	public Object invokeAction(ActivityEntity activity) {
		long points = 10;
		String UserKey = activity.getPlayerPlayground() + "@@" + activity.getPlayerEmail();
		UserEntity user = this.users.findById(UserKey)
				.orElseThrow(() -> new UserNotFoundException("no user found for: " + UserKey));
		// user verified check
		if (!user.isVerified())
			throw new RuntimeException("The user " + UserKey + " is not verified.");
		// user player check
		if ("Player".equalsIgnoreCase(user.getRole()))
			throw new RuntimeException("The user " + UserKey + " is not player.");
		// set key for element
		String element_key = activity.getElementPlayground() + "@@" + activity.getElementId();
		ElementEntity element = this.elements.findById(element_key)
				.orElseThrow(() -> new ElementNotFoundException("no Element for: " + element_key));

		if ("Animal".equalsIgnoreCase(element.getType())) {
			//add 10 points to user
			user.increasePoints(points);
			return new Message("the user " + UserKey +" pet a "+element.getName());
		} else {
			throw new RuntimeException("Not an Animal!");
		}

	}

}
