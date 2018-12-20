package playground.logic.jpa;

public class ElementNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 6587409435612889698L;

	public ElementNotFoundException() {
		super();
	}

	public ElementNotFoundException(String message) {
		super(message);
	}

	public ElementNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElementNotFoundException(Throwable cause) {
		super(cause);
	}

}
