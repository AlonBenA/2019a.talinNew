package playground.logic.jpa;

public class ElementAttributeNotValidException extends Exception {
	private static final long serialVersionUID = 7279715516750584372L;

	public ElementAttributeNotValidException() {
		super();
	}

	public ElementAttributeNotValidException(String message) {
		super(message);
	}

	public ElementAttributeNotValidException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElementAttributeNotValidException(Throwable cause) {
		super(cause);
	}

}
