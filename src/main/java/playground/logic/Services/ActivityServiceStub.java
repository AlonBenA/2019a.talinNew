package playground.logic.Services;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import playground.logic.Entities.ActivityEntity;


//@Service
public class ActivityServiceStub implements PlaygroundActivityService {
	private Map<String, ActivityEntity> activitiesDatabase;

	@PostConstruct
	public void init() {
		this.activitiesDatabase = new HashMap<>();
	}
	
	@Override
	public synchronized ActivityEntity addNewActivity(String userPlayground, String userEmail, ActivityEntity activityEntity) {
		this.activitiesDatabase.put(activityEntity.getPlayground() + "@@" + activityEntity.getId(), activityEntity);
		return activityEntity;
	}


	@Override
	public synchronized ActivityEntity getActivity(String userPlayground, String userEmail, String activity_id, String playground) throws Exception {
		ActivityEntity rv = this.activitiesDatabase.get(playground + "@@" + activity_id);
		if (rv == null) {
			throw new RuntimeException("could not find activity by id: " + playground + "@@" + activity_id);
		}
		return rv;
	}

	@Override
	public synchronized boolean validateActivityType(String type) {
		boolean result;

		switch (type) {
		case "ACO":
			result = true;
			break;
		case "feed": 
			result = true;
			break;

		default:
			result = false;
			break;
		}

		return result;
	}


	public synchronized Map<String, ActivityEntity> getActivitiesDatabase() {
		return activitiesDatabase;
	}

	public synchronized void setActivitiesDatabase(Map<String, ActivityEntity> activitiesDatabase) {
		this.activitiesDatabase = activitiesDatabase;
	}

	
	@Override
	public synchronized void cleanup() {
		this.activitiesDatabase.clear();
	}

}
