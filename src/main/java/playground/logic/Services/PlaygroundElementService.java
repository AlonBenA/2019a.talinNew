package playground.logic.Services;

import java.util.List;

import playground.logic.Entities.ElementEntity;
import playground.logic.Exceptions.ElementAlreadyExistException;
import playground.logic.Exceptions.ElementNotFoundException;

public interface PlaygroundElementService {
	
	public ElementEntity addNewElement(ElementEntity elementEntity) throws ElementAlreadyExistException;
	
	public ElementEntity getElement(String element_id,String element_Playground) throws ElementNotFoundException;
	
	public List<ElementEntity> getAllElements(int size, int page);
	
	public List<ElementEntity> getAllNearElements(double x,double y, double distance,int size,int page);
	
	public void updateElement(ElementEntity updatedElementEntity,String playground,String id) throws Exception;
	
	public boolean validateElementAttribteName(String name);
	
	public void cleanup();

}
