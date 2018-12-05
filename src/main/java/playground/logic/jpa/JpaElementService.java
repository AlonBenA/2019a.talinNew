package playground.logic.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import playground.jpadal.ElementDao;
import playground.jpadal.NumbersDao;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.GeneratedNumber;
import playground.logic.Exceptions.ElementAlreadyExistException;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.Services.PlaygroundElementService;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaElementService implements PlaygroundElementService {
			
	private NumbersDao numbers;
	private ElementDao elements;

	@Autowired
	public JpaElementService(NumbersDao numbers,ElementDao elements) {
		super();
		this.elements = elements;
		this.numbers = numbers;
	}

	@Override
	@Transactional
	public ElementEntity addNewElement(ElementEntity elementEntity) throws ElementAlreadyExistException {
		
		if(!elements.existsById(elementEntity.getKey()))
		{
			long number = this.numbers.save(new GeneratedNumber()).getNextValue();
			this.numbers.deleteById(number);			
			elementEntity.setId(number+"");		
			
			return this.elements.save(elementEntity);

		}else {
			throw new ElementAlreadyExistException("elementEntity exisits with: " + elementEntity.getKey());
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public ElementEntity getElement(String element_id, String element_Playground) throws ElementNotFoundException {
		// set key for element 
		String element_key = element_Playground + "@@" +element_id;
		
		return 
				this.elements.findById(element_key)
				.orElseThrow(()->new ElementNotFoundException("no Element for: " + element_key));

	}

	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> getAllElements(int size, int page) {
		
		return 
		this.elements.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate"))
			.getContent();
	}

	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> getAllNearElements(double x, double y, double distance, int size, int page) {
		
		Double upperX = (x+distance);
		Double lowerX = (x-distance);
		Double upperY = (y+distance);
		Double lowerY = (y-distance);				
		
		return this.elements.findAllByXLessThanAndXGreaterThanAndYLessThanAndYGreaterThan(upperX,lowerX,upperY,lowerY
				,PageRequest.of(
				page, 
				size, 
				Direction.DESC, 
				"creationDate"))
		.getContent();

		
		/*
				this.messages
					.findAllByMoreAttributesJsonLike(
						"%\"abc\":" + value + "%", 
						PageRequest.of(
							page, 
							size, 
							Direction.DESC, 
							"creationDate"));

		List<ElementEntity> allList = new ArrayList<>();

		this.elements.findAll()
			.forEach(allList::add);
		
		return allList
				.stream() // stream of entities
				.filter(ent -> Math.abs(ent.getX() - x) < distance)
				.filter(ent -> Math.abs(ent.getY() - y) < distance)
				.skip(size * page)
				.limit(size)
				.collect(Collectors.toList());
		

		return this.elements.findAllByLocationJsonIsNotNullAndLocationJsonLessThanAndLocationJsonGreaterThanAndLocationJsonLessThanAndLocationJsonGreaterThan(
				"%\"x\":" + upperX + "%",
				"%\"x\":" + lowerX + "%",
				"%\"y\":" + upperY + "%",
				"%\"y\":" + lowerY + "%",
				PageRequest.of(
						page, 
						size, 
						Direction.DESC, 
						"creationDate"))
				.getContent();
	
		*/	
				
	}

	@Override
	@Transactional
	public void updateElement(ElementEntity updatedElementEntity, String playground, String id) throws Exception {
		
		String element_key = playground + "@@" +id;
		
		ElementEntity existing = getElement(id, playground);
		
		
		
		if (updatedElementEntity.getX() != null) {
			existing.setX(updatedElementEntity.getX());
		}
		
		if (updatedElementEntity.getY() != null) {
			existing.setY(updatedElementEntity.getY());
		}
		
		
		if (updatedElementEntity.getName() != null) {
			existing.setName(updatedElementEntity.getName());
		}
		
		if (updatedElementEntity.getExirationDate() != null) {
			existing.setExirationDate(updatedElementEntity.getExirationDate());
		}
		
		if (updatedElementEntity.getType() != null) {
			existing.setType(updatedElementEntity.getType());
		}
		
		if (updatedElementEntity.getAttributes() != null) {
			existing.setAttributes(updatedElementEntity.getAttributes());
		}
		
		this.elements.save(existing);
		
	}

	@Override
	public boolean validateElementAttribteName(String name) {
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

	@Override
	@Transactional
	public void cleanup() {
		
		this.elements.deleteAll();
	}
	
	
	

}
