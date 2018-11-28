package playground.logic.Exceptions;

public class ElementNotFoundException extends Exception {
	private static final long serialVersionUID = -4402147946901277231L;

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
