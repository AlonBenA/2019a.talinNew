package playground.logic.Services;


import java.util.List;
import playground.logic.Entities.ElementEntity;
import playground.logic.jpa.ElementAlreadyExistException;
import playground.logic.jpa.ElementAttributeNotValidException;
import playground.logic.jpa.ElementNotFoundException;

public interface PlaygroundElementService {
	
	public ElementEntity addNewElement(String userPlayground, String email, ElementEntity elementEntity) throws ElementAlreadyExistException;
	
	public ElementEntity getElement(String userPlayground, String email,String element_id,String element_Playground) throws ElementNotFoundException;
	
	public List<ElementEntity> getAllElements(String userPlayground,String email,int size, int page);
	
	public List<ElementEntity> getAllNearElements(String userPlayground,String email,double x,double y, double distance,int size,int page);
	
	public void updateElement(String userPlayground,String email,ElementEntity updatedElementEntity,String playground,String id) throws Exception;
	
	public void validateElementAttribteName(String name) throws ElementAttributeNotValidException;
	
	public void cleanup();

	public List<ElementEntity> getElementsWithAttribute(String userPlayground,String email, String attributeName, String value, int size, int page);

}
