package playground.logic.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.MyLogger;
import playground.aop.UserVerifiedAndExistCheck;
import playground.jpadal.UserDao;
import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundUserService;

@Service
public class JpaUserService implements PlaygroundUserService{
	private UserDao users;
	
	@Autowired
	public JpaUserService(UserDao users) {
		super();
		this.users = users;
	}
	
	
	@Override
	@Transactional
	public UserEntity addNewUser(UserEntity userEntity) {
		if (!this.users.existsById(userEntity.getKey())) {
			System.err.println("user: " + userEntity.getPlayground() + "@@" + userEntity.getEmail() + " code: "
					+ userEntity.getCode()); // "send" code
			return this.users.save(userEntity);
		}
		throw new UserAlreadyExistsException("message exists with: " + userEntity.getKey()); 
		
	}

	@Override
	public UserEntity getUser(String playground, String email) throws UserNotFoundException {
		String key = playground+"@@"+email;
		return this.users.findById(key)
				.orElseThrow(()->new UserNotFoundException("no user found for: " + key));
		
	}
	
	@Override
	public UserEntity validateUser(UserEntity userEntity, String code) throws UserNotFoundException {
		UserEntity existingUser = getUser(userEntity.getPlayground(),userEntity.getEmail());
		if (existingUser != null) {
			existingUser.verify(code);
			if(existingUser.isVerified()) 
				return this.users.save(existingUser);
				
			throw new RuntimeException("Wrong code");
		}
		throw new UserNotFoundException("no user found for: " + userEntity.getKey());
		
	}
	
	/////////////////////////////////////////////////////
	@Override
	public UserEntity userLogin(String playground, String email) throws RuntimeException {
		UserEntity existingUser = getUser(playground,email);
		if(!existingUser.isVerified())
			throw new RuntimeException("The user "+ existingUser.getKey() + " is not verified");
		return existingUser;
	}
	/////////////////////////////////////////////////////

	@Override
	@Transactional
	@MyLogger
	public void updateUser(String playground,String email, UserEntity updatedUserEntity  ) throws Exception {
		UserEntity existing = userLogin(playground,email);
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
