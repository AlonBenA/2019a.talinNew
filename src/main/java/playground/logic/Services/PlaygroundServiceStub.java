package playground.logic.Services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import playground.logic.Location;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.Exceptions.UserNotFoundException;

@Service
public class PlaygroundServiceStub implements PlaygroundService {
	private Map<String, UserEntity> usersDatabase;
	private Map<String, ElementEntity> elementsDatabase;
	private Map<String, ActivityEntity> activitiesDatabase;

	@PostConstruct
	public void init() {
		this.usersDatabase = new HashMap<>();
		this.elementsDatabase = new HashMap<>();
		this.activitiesDatabase = new HashMap<>();
	}

	public synchronized void setElementsDatabase(Map<String, ElementEntity> elementsDatabase) {
		Date exirationDate = null;
		String type = "animal";
		String creatorPlayground = "2019a.talin";
		String creatorEmail = "2019a.Talin@Gmail.com";
		Map<String, Object> attributes = new HashMap<>();

		// add specific attribute
		Random rand = new Random();
		if (rand.nextInt(100) < 20) { // 20% of the elements
			attributes.put("Eat", "meat");
		}

		// location,value,exirationDate,type,attributes,creatorPlayground,creatorEmail
		this.elementsDatabase = IntStream.range(0, 100) // int stream
				.mapToObj(value -> new ElementEntity(new Location(value, value), "animal #" + value,
						exirationDate, type, attributes, creatorPlayground, creatorEmail)) // ElementTO stream using
																							// constructor reference
				.collect(Collectors.toMap(ElementEntity::getId, Function.identity()));
	}

	@Override
	public synchronized ElementEntity addNewElement(ElementEntity elementEntity) {
		String key = elementEntity.getPlayground() + elementEntity.getId();
		this.elementsDatabase.put(key, elementEntity);
		return this.elementsDatabase.get(key);
	}
	
	
	@Override
	public synchronized ActivityEntity addNewActivity(ActivityEntity activityEntity) {
		this.activitiesDatabase.put(activityEntity.getPlayground() + activityEntity.getId(), activityEntity);
		return activityEntity;
	}

	@Override
	public synchronized ElementEntity getElement(String element_id, String element_Playground) throws ElementNotFoundException {
		ElementEntity rv = this.elementsDatabase.get(element_Playground + element_id);
		if (rv == null) {
			throw new ElementNotFoundException("could not find element by id: " + element_Playground + element_id);
		}
		return rv;
	}

	@Override
	public synchronized ActivityEntity getActivity(String activity_id, String playground) throws Exception {
		ActivityEntity rv = this.activitiesDatabase.get(playground + activity_id);
		if (rv == null) {
			throw new RuntimeException("could not find activity by id: " + playground + activity_id);
		}
		return rv;
	}

	@Override
	public synchronized List<ElementEntity> getAllElements(int size, int page) {
		return this.elementsDatabase.values() // collection of entities
				.stream() // stream of entities
				.skip(size * page).limit(size).collect(Collectors.toList());
	}

	@Override
	public synchronized boolean validateActivityType(String type) {
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
	public synchronized boolean validateElementAttribteName(String name) {
		boolean result;

		switch (name) {
		case "name":
			result = true;
			break;

		case "type":
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

	public synchronized Map<String, ElementEntity> getElementsDatabase() {
		return elementsDatabase;
	}

	public synchronized List<ElementEntity> getAllNearElements(double x, double y, double distance, int size,
			int page) {
		return this.elementsDatabase.values().stream() // stream of entities
				.filter(ent -> Math.abs(ent.getLocation().getX() - x) < distance)
				.filter(ent -> Math.abs(ent.getLocation().getY() - y) < distance).skip(size * page).limit(size)
				.collect(Collectors.toList());
	}

	@Override
	public synchronized void updateElement(ElementEntity updatedElementEntity, String playground, String id)
			throws Exception {

		if (this.elementsDatabase.containsKey(playground + id)) {
			ElementEntity elementEntity = this.elementsDatabase.get(playground + id);

			if (elementEntity.getLocation() != null && !elementEntity.getLocation().equals(updatedElementEntity.getLocation())) {
				elementEntity.setLocation(updatedElementEntity.getLocation());
			}

			if (elementEntity.getName() != null && !elementEntity.getName().equals(updatedElementEntity.getName())) {
				elementEntity.setName(updatedElementEntity.getName());
			}

			if (elementEntity.getExirationDate() != null && !elementEntity.getExirationDate().equals(updatedElementEntity.getExirationDate())) {
				elementEntity.setExirationDate(updatedElementEntity.getExirationDate());
			}

			if (elementEntity.getType() != null && !elementEntity.getType().equals(updatedElementEntity.getType())) {
				elementEntity.setType(updatedElementEntity.getType());
			}

			if (elementEntity.getAttributes() != null && !elementEntity.getAttributes().equals(updatedElementEntity.getAttributes())) {
				elementEntity.setAttributes(updatedElementEntity.getAttributes());
			}

			this.elementsDatabase.put(playground + id, elementEntity);

		} else {
			throw new ElementNotFoundException("Did not found the element");
		}

	}

	@Override
	public synchronized UserEntity addNewUser(UserEntity userEntity) {
		String key = userEntity.getPlayground() + userEntity.getEmail();
		if (this.usersDatabase.containsKey(key)) {
			throw new RuntimeException("a user already exists for: " + key);
		}
		this.usersDatabase.put(key, userEntity);
		return this.usersDatabase.get(key);
	}

	@Override
	public synchronized UserEntity getUser(String email, String playground) throws UserNotFoundException {
		String key = playground + email;
		UserEntity userEntity = this.usersDatabase.get(key);
		if (userEntity == null) {
			throw new RuntimeException("could not find user by id: " + key);
		}
		return userEntity;
	}
	
	

	@Override
	public synchronized void updateUser(UserEntity updatedUserEntity,String email,String playground) throws Exception {

		if (this.usersDatabase.containsKey(playground + email)) {
			UserEntity userEntity = this.usersDatabase.get(playground + email);

			if (userEntity.getUsername() != null && !userEntity.getUsername().equals(updatedUserEntity.getUsername())) {
				userEntity.setUsername(updatedUserEntity.getUsername());
			}

			if (userEntity.getAvatar() != null && !userEntity.getAvatar().equals(updatedUserEntity.getAvatar())) {
				userEntity.setAvatar(updatedUserEntity.getAvatar());
			}
			
			if (userEntity.getRole() != null && !userEntity.getRole().equals(updatedUserEntity.getRole())) {
				userEntity.setRole(updatedUserEntity.getRole());
			}
			
			this.usersDatabase.put(playground + email, userEntity);

		} else {
			throw new ElementNotFoundException("Did not found the element");
		}

	}
	
	
	@Override
	public synchronized void cleanup() {
		this.elementsDatabase.clear();
		this.activitiesDatabase.clear();
		this.usersDatabase.clear();
	}

}
