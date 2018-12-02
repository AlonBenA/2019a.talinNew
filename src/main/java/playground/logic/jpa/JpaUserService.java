package playground.logic.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import playground.jpadal.UserDao;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.UserAlreadyExistsException;
import playground.logic.Exceptions.UserNotFoundException;
import playground.logic.Services.PlaygroundUserService;

//@Service
public class JpaUserService implements PlaygroundUserService{
	private UserDao users;
	
	@Autowired
	public JpaUserService(UserDao users) {
		super();
		this.users = users;
	}
	
	
	@Override
	public UserEntity addNewUser(UserEntity userEntity) {
		if (!this.users.existsById(userEntity.getKey())) 
			return this.users.save(userEntity);
		throw new UserAlreadyExistsException("message exists with: " + userEntity.getKey()); 
		
//		throw new UserAlreadyExistsException("message exists with: "); // remove
	}

	@Override
	public UserEntity getUser(String email, String playground) throws UserNotFoundException {
		String key = playground+"@@"+email;
		return this.users.findById(key)
				.orElseThrow(()->new UserNotFoundException("no user found for: " + key));
		
//		throw new UserAlreadyExistsException("message exists with: "); // remove
	}

	@Override
	@Transactional
	public void updateUser(UserEntity updatedUserEntity, String email, String playground) throws Exception {
		UserEntity existing = getUser(email, playground);
		
		if (updatedUserEntity.getUsername() != null) {
			existing.setUsername(updatedUserEntity.getUsername());
		}
		
		if (updatedUserEntity.getAvatar() != null) {
			existing.setAvatar(updatedUserEntity.getAvatar());
		}
		
		if (updatedUserEntity.getRole() != null) {
			existing.setRole(updatedUserEntity.getRole());
		}
		
		this.users.save(existing);
	}

	@Override
	@Transactional
	public void cleanup() {
		this.users.deleteAll();
	}
	
}
