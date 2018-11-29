package playground.logic.Services;

import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.UserNotFoundException;

public interface PlaygroundUserService {

	public UserEntity addNewUser(UserEntity userEntity);

	public UserEntity getUser(String email, String playground) throws UserNotFoundException;

	public void updateUser(UserEntity updatedUserEntity, String email, String playground) throws Exception;

	public void cleanup();
}
