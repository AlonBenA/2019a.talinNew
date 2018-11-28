package playground.logic.Services.jpa;


import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import playground.jpadal.ActivityDao;
import playground.jpadal.ElementDao;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Services.PlaygroundActivityService;

@Service
public class JpaActivityService implements PlaygroundActivityService {
	private ActivityDao activities;
	private ElementDao elements;
	//private NumbersDao numbers;

	@Autowired
	public JpaActivityService(ActivityDao activities, ElementDao elements/*, NumbersDao numbers*/) {
		super();
		this.activities = activities;
		this.elements = elements;
		//this.numbers = numbers
	}

	
	@Override
	@Transactional
	public ActivityEntity addNewActivity(ActivityEntity activityEntity) {
		if (!this.activities.existsById(activityEntity.getKey())) {
			
			// check if the element to activate exists
			String element_key = activityEntity.getElementPlayground() + "@@" +
			activityEntity.getElementId();
			if (this.elements.existsById(element_key)) {
				//long number = this.numbers.save(new GeneratedNumber()).getNextValue();
				//this.numbers.deleteById(number);
				//activityEntity.setId("" + number);
				return this.activities.save(activityEntity);
			}else
				throw new RuntimeException("The element to activate with key: " + element_key
						+ " not exsists");
		}else {
			throw new RuntimeException("Activity already exisits with: " + activityEntity.getKey()); 
		}
	}


	@Override
	@Transactional(readOnly=true)
	public ActivityEntity getActivity(String activity_key){
		return 
				this.activities.findById(activity_key)
				.orElseThrow(()->new RuntimeException("no activity for: " + activity_key));
	}


	@Override
	public  boolean validateActivityType(String type) {
		boolean result;

		switch (type) {
		case "ACO":
			result = true;
			break;

		default:
			result = false;
			break;
		}

		return result;
	}
	
		
	@Override
	public synchronized void cleanup() {
		this.activities.deleteAll();
	}

}
