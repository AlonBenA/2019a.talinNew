package playground.logic.Services;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import playground.logic.Entities.UserEntity;
import playground.logic.jpa.ElementNotFoundException;
import playground.logic.jpa.UserNotFoundException;

//@Service
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
	public synchronized UserEntity getUser(String playground, String email) throws UserNotFoundException {
		String key = playground + email;
		UserEntity userEntity = this.usersDatabase.get(key);
		if (userEntity == null) {
			throw new RuntimeException("could not find user by id: " + key);
		}
		return userEntity;
	}
	
	@Override
	public UserEntity validateUser(UserEntity userEntity, String code) throws UserNotFoundException {
		UserEntity existingUser = this.usersDatabase.get(userEntity.getKey());
		if (existingUser != null) {
			existingUser.verify(code);
			if(existingUser.isVerified()) 
				return this.usersDatabase.put(existingUser.getKey(), existingUser);
				
			throw new RuntimeException("Wrong code");
		}
		throw new UserNotFoundException("no user found for: " + userEntity.getKey());
		
	}
	
	/////////////////////////////////////////////////////
	@Override
	public UserEntity userLogin(String playground, String email) throws UserNotFoundException {
		// TODO
		return null;
	
	}
	/////////////////////////////////////////////////////
	
	

	@Override
	public synchronized void updateUser(String playground,String email, UserEntity updatedUserEntity) throws Exception {

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
