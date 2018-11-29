package playground.logic.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import playground.logic.Entities.ElementEntity;
import playground.logic.Exceptions.ElementNotFoundException;


@Service
public class ElementServiceStub implements PlaygroundElementService  {
	
	private Map<String, ElementEntity> elementsDatabase;
	
	@PostConstruct
	public void init() {
		this.elementsDatabase = new HashMap<>();
	}

	@Override
	public ElementEntity addNewElement(ElementEntity elementEntity) {
		String key = elementEntity.getPlayground() + elementEntity.getId();
		this.elementsDatabase.put(key, elementEntity);
		return this.elementsDatabase.get(key);
	
	}

	@Override
	public synchronized ElementEntity getElement(String element_id, String element_Playground) throws ElementNotFoundException {
		ElementEntity rv = this.elementsDatabase.get(element_Playground + element_id);
		if (rv == null) {
			throw new ElementNotFoundException("could not find element by id: " + element_Playground + element_id);
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
				.filter(ent -> Math.abs(ent.getLocation().getX() - x) < distance)
				.filter(ent -> Math.abs(ent.getLocation().getY() - y) < distance).skip(size * page).limit(size)
				.collect(Collectors.toList());
	}


	@Override
	public synchronized void updateElement(ElementEntity updatedElementEntity, String playground, String id)
			throws Exception {

		if (this.elementsDatabase.containsKey(playground + id)) {
			ElementEntity elementEntity = this.elementsDatabase.get(playground + id);

			if (elementEntity.getLocation() != null && !elementEntity.getLocation().equals(updatedElementEntity.getLocation())) {
				elementEntity.setLocation(updatedElementEntity.getLocation());
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
