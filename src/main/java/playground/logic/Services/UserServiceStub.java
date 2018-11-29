package playground.logic.Services;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.Exceptions.UserNotFoundException;

@Service
public class UserServiceStub implements PlaygroundUserService {
	private Map<String, UserEntity> usersDatabase;
	
	@PostConstruct
	public void init() {
		this.usersDatabase = new HashMap<>();
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
		this.usersDatabase.clear();
	}


}
