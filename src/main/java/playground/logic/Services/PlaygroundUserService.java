package playground.logic.Services;

import playground.logic.Entities.UserEntity;
import playground.logic.jpa.UserNotFoundException;

public interface PlaygroundUserService {
	
	public UserEntity addNewUser(UserEntity userEntity);
	
	public UserEntity getUser(String playground, String email) throws UserNotFoundException;
	
	public UserEntity validateUser(UserEntity userEntity, String code) throws UserNotFoundException;
	
	public UserEntity userLogin(String playground, String email) throws RuntimeException;
	
	public void updateUser( String playground, String email,UserEntity updatedUserEntity) throws Exception;
	
	public void cleanup();
}
