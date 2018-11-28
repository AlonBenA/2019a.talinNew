package playground.logic.Services;

import java.util.List;

import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.Exceptions.UserNotFoundException;

public interface PlaygroundService {
	
	public ElementEntity addNewElement(ElementEntity elementEntity);
	
	public UserEntity addNewUser(UserEntity userEntity);
	
	public ActivityEntity addNewActivity(ActivityEntity activityEntity);

	public UserEntity getUser(String email, String playground) throws UserNotFoundException;
	
	public ElementEntity getElement(String element_id,String element_Playground) throws ElementNotFoundException;
	
	public ActivityEntity getActivity(String activity_id, String playground) throws Exception;
	
	public void cleanup();

	public List<ElementEntity> getAllElements(int size, int page);
	
	public boolean validateActivityType(String type);
	
	public List<ElementEntity> getAllNearElements(double x,double y, double distance,int size,int page);
	
	public void updateElement(ElementEntity updatedElementEntity,String playground,String id) throws Exception;
	
	public void updateUser(UserEntity updatedUserEntity,String email,String playground) throws Exception;
	
	public boolean validateElementAttribteName(String name);
}
