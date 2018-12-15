package playground.layout.WebUI;


import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import playground.layout.ElementTO;
import playground.layout.ErrorMessage;
import playground.logic.Services.PlaygroundElementService;
import playground.logic.jpa.ElementAttributeNotValidException;
import playground.logic.jpa.ElementNotFoundException;
import playground.logic.jpa.UserNotFoundException;

@RestController
public class WebUIElement {
	
	private PlaygroundElementService elementService;

	
	@Autowired
	public void setPlaygroundService(PlaygroundElementService elementService) {
		this.elementService = elementService;
	}

	
	private void validateNull(String name) throws Exception {
		if ("null".equals(name) || name == null) {
			throw new Exception("user not found");
		}
	}

	//Sprint2: Write the GET/playground/elements/{userPlayground}/{email}/search/{attributeNa me}/{value} 
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsWithAttribute(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("attributeName") String attributeName,
			@PathVariable("value") String value,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		
		//validate user
		validateNull(email);
		validateNull(userPlayground);
		
		// validate attribute
		elementService.validateElementAttribteName(attributeName);
		
		return elementService.getElementsWithAttribute(attributeName, value, size, page)
		.stream()
		.map(ElementTO::new)
		.collect(Collectors.toList())
		.toArray(new ElementTO[0]);				
	}
	
	
	//Sprint2: Write the GET /playground/elements/{userPlayground}/{email}/all
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/all",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements (@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
			
		try {		
				return elementService.getAllElements(userPlayground,email,size, page) // list of entities
				.stream() // stream of entities
				.map(ElementTO::new) // stream of boundaries
				.collect(Collectors.toList())// list of boundaries
				.toArray(new ElementTO[0]); // ElementTO[]
			
		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving data");
		}		
	}
	
	
	//Sprint2: Write the GET /playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}
	// to check {distance} Represents 
	@RequestMapping(
			method=RequestMethod.GET,
			path= "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}" ,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllNearElements (@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("x") double x,
			@PathVariable("y") double y,
			@PathVariable("distance") double center,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) throws Exception {
		
			return elementService.getAllNearElements(userPlayground,email,x, y, center, size, page)
					.stream()
					.map(ElementTO::new)
					.collect(Collectors.toList())
					.toArray(new ElementTO[0]);
			
	}
	
	//Sprint2: Write the PUT /playground/elements/{userPlayground}/{email}/{playground}/{id}
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateElement (
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("playground") String playground,
			@PathVariable("id") String id,
			@RequestBody ElementTO updatedElement) throws Exception {
		
		elementService.updateElement(userPlayground,email,updatedElement.convertFromElementTOToElementEntity(), playground, id);
		
	}
	
	
	//Sprint2: Write the POST /playground/elements/{userPlayground }/{email}
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/elements/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO createElement (
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@RequestBody ElementTO elementTO) throws Exception {
		
		//set to elementTo the CreatorPlayground and CreatorEmail from URL
		elementTO.setCreatorEmail(email);
		elementTO.setCreatorPlayground(userPlayground);
		
		return new ElementTO(
				this.elementService.addNewElement(userPlayground,email,elementTO.convertFromElementTOToElementEntity()));
	}
	
	
	//Sprint2: Write the GET /playground/elements/{userPlayground}/{email}/{playground}/{id}
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElement (
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) throws ElementNotFoundException {
		return new ElementTO(this.elementService.getElement(userPlayground,email,id, playground));	
	}
	
	@ExceptionHandler//(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException (UserNotFoundException e) {
		String errorMessage = e.getMessage();
		if (errorMessage == null) {
			errorMessage = "There is no relevant message";
		}
		return new ErrorMessage(errorMessage);
	}	
	
	@ExceptionHandler//(ElementNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException (ElementNotFoundException e) {
		String errorMessage = e.getMessage();
		if (errorMessage == null) {
			errorMessage = "There is no relevant message";
		}
		return new ErrorMessage(errorMessage);
	}
	
}









