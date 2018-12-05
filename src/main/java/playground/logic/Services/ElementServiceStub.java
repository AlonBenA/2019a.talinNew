package playground.logic.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import playground.logic.Entities.ElementEntity;
import playground.logic.Exceptions.ElementNotFoundException;


//@Service
public class ElementServiceStub implements PlaygroundElementService  {	
	
	private static AtomicLong IDGiver = new AtomicLong(0);
	
	
	private String get_ID()
	{
		return IDGiver.getAndIncrement()+"";
	}
	
	public static void resetID()
	{
		IDGiver = new AtomicLong(0);
	}
	
	private Map<String, ElementEntity> elementsDatabase;
	
	@PostConstruct
	public void init() {
		this.elementsDatabase = new HashMap<>();
	}

	@Override
	public ElementEntity addNewElement(ElementEntity elementEntity) {
		
		elementEntity.setId(get_ID());
		String key = elementEntity.getKey();
		this.elementsDatabase.put(key, elementEntity);
		return this.elementsDatabase.get(key);
	
	}

	@Override
	public synchronized ElementEntity getElement(String element_id, String element_Playground) throws ElementNotFoundException {
		
		String key = element_Playground + "@@" + element_id;
		
		ElementEntity rv = this.elementsDatabase.get(key);
		if (rv == null) {
			throw new ElementNotFoundException("could not find element by id: " + key);
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
	public synchronized List<ElementEntity> getAllNearElements(double x, double y, double distance, int size,
			int page) {
		return this.elementsDatabase.values().stream() // stream of entities
				.filter(ent -> Math.abs(ent.getX() - x) < distance)
				.filter(ent -> Math.abs(ent.getY() - y) < distance).skip(size * page).limit(size)
				.collect(Collectors.toList());
	}


	@Override
	public synchronized void updateElement(ElementEntity updatedElementEntity, String playground, String id)
			throws Exception {
		
		String key = playground + "@@" + id;

		if (this.elementsDatabase.containsKey(key)) {
			ElementEntity elementEntity = this.elementsDatabase.get(key);

			if (elementEntity.getX() != null && !elementEntity.getX().equals(updatedElementEntity.getX())) {
				elementEntity.setX(updatedElementEntity.getX());
			}
			
			if (elementEntity.getY() != null && !elementEntity.getY().equals(updatedElementEntity.getY())) {
				elementEntity.setY(updatedElementEntity.getY());
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
	
	public synchronized Map<String, ElementEntity> getElementsDatabase() {
		return elementsDatabase;
	}
	
	
	@Override
	public void cleanup() {
		this.elementsDatabase.clear();
		
	}

}
