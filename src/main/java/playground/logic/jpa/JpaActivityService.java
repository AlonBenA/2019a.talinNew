package playground.logic.jpa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.MyLogger;
import playground.aop.PlayerExistCheck;
import playground.jpadal.ActivityDao;
import playground.jpadal.ElementDao;
import playground.jpadal.NumbersDao;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.GeneratedNumber;
import playground.logic.Services.PlaygroundActivityService;
import playground.plugins.PlaygroungActivityPlugin;


@Service
public class JpaActivityService implements PlaygroundActivityService {

	private ActivityDao activities;
	private NumbersDao numbers;
	private ElementDao elements;
	
	private ConfigurableApplicationContext spring;

	@Autowired
	public JpaActivityService(ActivityDao activities, NumbersDao numbers, ElementDao elements, ConfigurableApplicationContext spring) {
		super();
		this.activities = activities;
		this.elements = elements;
		this.numbers = numbers;
		this.spring = spring;
	}

	@Override
	@Transactional
	@PlayerExistCheck
	@MyLogger
	public Object addNewActivity(String userPlayground, String userEmail, ActivityEntity activityEntity) {
		if (!activities.existsById(activityEntity.getKey())) {
			// check if the element to activate exists
			String element_key = activityEntity.getElementPlayground() + "@@" + activityEntity.getElementId();
			if (this.elements.existsById(element_key)) {

				long number = this.numbers.save(new GeneratedNumber()).getNextValue();
				this.numbers.deleteById(number);
				activityEntity.setId(number + "");
				Object rv = null;
				// Make the Action
				if (activityEntity.getType() != null) {
					try {
						String type = activityEntity.getType();
						String targetClassName = "playground.plugins." + type + "ActivityPlugin";
						Class<?> pluginClass = Class.forName(targetClassName);
						// autowire plugin
						PlaygroungActivityPlugin plugin = (PlaygroungActivityPlugin) this.spring.getBean(pluginClass);
						ElementEntity elementToActivate = plugin.checkAction(activityEntity);
						rv = plugin.invokeAction(activityEntity, activityEntity.getId(), elementToActivate);
						activityEntity.getAttributes().put("Message", rv);
						this.activities.save(activityEntity);
						
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				return rv;
			} else
				throw new RuntimeException("The element to activate with key: " + element_key + " not exsists");

		} else {
			throw new RuntimeException("Activity already exisits with: " + activityEntity.getKey());
		}
	}

	@Override
	@Transactional(readOnly = true)
	@MyLogger
	public ActivityEntity getActivity(String userPlayground, String userEmail, String activity_id, String playground) {
		// set key for element
		String activity_key = playground + "@@" + activity_id;
		return this.activities.findById(activity_key)
				.orElseThrow(() -> new RuntimeException("no activity for: " + activity_key));
	}

	@Override
	@MyLogger
	public boolean validateActivityType(String type) {
		boolean result;

		switch (type) {
		case "Feed":
			result = true;
			break;
		case "Pet":
			result = true;
			break;
		case "PostMessage":
			result = true;
			break;
		case "ReadFromBoard":
			result = true;
			break;

		default:
			throw new ActivityTypeNotSupportedException("Invalid Activity Type");
		}

		return result;
	}

	@Override
	@MyLogger
	public synchronized void cleanup() {
		this.activities.deleteAll();
	}

}
