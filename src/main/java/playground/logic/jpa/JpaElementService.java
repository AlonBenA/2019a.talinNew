package playground.logic.jpa;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import playground.aop.ManagerExistCheck;
import playground.aop.MyLogger;
import playground.aop.UserVerifiedAndExistCheck;
import playground.jpadal.ElementDao;
import playground.jpadal.NumbersDao;
import playground.jpadal.UserDao;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.GeneratedNumber;
import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundElementService;
import java.util.ArrayList;
import java.util.Date;

@Service
public class JpaElementService implements PlaygroundElementService {

	private NumbersDao numbers;
	private ElementDao elements;
	private UserDao users;

	@Autowired
	public JpaElementService(NumbersDao numbers, ElementDao elements, UserDao users) {
		super();
		this.elements = elements;
		this.users = users;
		this.numbers = numbers;
	}

	@Override
	@Transactional
	@MyLogger
	@ManagerExistCheck
	public ElementEntity addNewElement(String userPlayground, String email, ElementEntity elementEntity)
			throws ElementAlreadyExistException {

		if (!elements.existsById(elementEntity.getKey())) {
			long number = this.numbers.save(new GeneratedNumber()).getNextValue();
			this.numbers.deleteById(number);
			elementEntity.setId(number + "");

			return this.elements.save(elementEntity);

		} else {
			throw new ElementAlreadyExistException("elementEntity exisits with: " + elementEntity.getKey());
		}

	}

	@Override
	@Transactional(readOnly = true)
	@MyLogger
	@UserVerifiedAndExistCheck
	public ElementEntity getElement(String userPlayground, String email, String element_id, String element_Playground)
			throws ElementNotFoundException {
		// set key for element
		String element_key = element_Playground + "@@" + element_id;
		ElementEntity elementExist = this.elements.findById(element_key)
				.orElseThrow(() -> new ElementNotFoundException("no Element for: " + element_key));

		if ("Player".equalsIgnoreCase(getUserRole(userPlayground, email)) && elementExist.getExirationDate()!=null) {
			Date today = new Date();
			
			if (elementExist.getExirationDate().before(today))
				throw new ElementNotFoundException("no Element for: " + element_key);
		}
		return elementExist;
	}

	@Override
	@Transactional(readOnly = true)
	@UserVerifiedAndExistCheck
	@MyLogger
	public List<ElementEntity> getAllElements(String userPlayground, String email, int size, int page) {

		if ("Manager".equalsIgnoreCase(getUserRole(userPlayground, email))) {
			// if it's a Manager
			return this.elements.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
		} else {
			// if it's a Client
			Date today = new Date();
			return this.elements.findAllByExirationDateIsNullOrExirationDateAfter(today,
					PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
		}

	}

	@Override
	@Transactional(readOnly = true)
	@UserVerifiedAndExistCheck
	@MyLogger
	public List<ElementEntity> getAllNearElements(String userPlayground, String email, double x, double y,
			double distance, int size, int page) {

		Double upperX = (x + distance);
		Double lowerX = (x - distance);
		Double upperY = (y + distance);
		Double lowerY = (y - distance);

		if ("Manager".equalsIgnoreCase(getUserRole(userPlayground, email))) {
			// if it's a Manager
			return this.elements.findAllByXLessThanAndXGreaterThanAndYLessThanAndYGreaterThan(upperX, lowerX, upperY,
					lowerY, PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
		} else {
			// if it's a Client
			Date today = new Date();
			return this.elements
					.findAllByExirationDateIsNullAndXLessThanAndXGreaterThanAndYLessThanAndYGreaterThanOrExirationDateAfterAndXLessThanAndXGreaterThanAndYLessThanAndYGreaterThan(
							upperX, lowerX, upperY, lowerY, today, upperX, lowerX, upperY, lowerY,
							PageRequest.of(page, size, Direction.DESC, "creationDate"))
					.getContent();
		}

	}

	@Override
	@Transactional
	@ManagerExistCheck
	@MyLogger
	public void updateElement(String userPlayground, String email, ElementEntity updatedElementEntity,
			String playground, String id) throws Exception {

		ElementEntity existing = getElement(userPlayground, email, id, playground);

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
	@MyLogger
	public void validateElementAttribteName(String name) throws ElementAttributeNotValidException {
		if (!name.equals("name") && !name.equals("type"))
			throw new ElementAttributeNotValidException("Invalid Attribute for searching elements");
	}

	@Override
	@Transactional
	@MyLogger
	public void cleanup() {

		this.elements.deleteAll();
	}

	@Override
	@UserVerifiedAndExistCheck
	@MyLogger
	public List<ElementEntity> getElementsWithAttribute(String userPlayground, String email, String attributeName,
			String value, int size, int page) {

		if ("Manager".equalsIgnoreCase(getUserRole(userPlayground, email))) { // Manager
			// if attribute is "name"
			if (attributeName.equals("name")) {
				return this.elements
						.findAllByNameLike(value, PageRequest.of(page, size, Direction.DESC, "creationDate"))
						.getContent();
			} else {
				// attribute is "type"
				return this.elements
						.findAllByTypeLike(value, PageRequest.of(page, size, Direction.DESC, "creationDate"))
						.getContent();
			}
		} else { // Player

			Date today = new Date();
			if (attributeName.equals("name")) {
				List<ElementEntity> elementsList = new ArrayList<>();
				elementsList.addAll(this.elements.findAllByExirationDateIsNullAndNameLike(value,
						PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent());
				elementsList.addAll(this.elements.findAllByExirationDateAfterAndNameLike(today, value,
						PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent());
				return elementsList;

			} else { // attribute is "type"
				List<ElementEntity> elementsList = new ArrayList<>();
				elementsList.addAll(this.elements.findAllByExirationDateIsNullAndTypeLike(value,
						PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent());
				elementsList.addAll(this.elements.findAllByExirationDateAfterAndTypeLike(today, value,
						PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent());
				return elementsList;
			}
		}

	}

	@Override
	public String getUserRole(String userPlayground, String email) throws UserNotFoundException {
		String key = userPlayground + "@@" + email;
		UserEntity User = this.users.findById(key)
				.orElseThrow(() -> new UserNotFoundException("no user found for: " + key));

		return User.getRole();
	}

}
