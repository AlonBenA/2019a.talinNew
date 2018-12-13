package playground.logic.jpa;

public class ElementAlreadyExistException extends Exception{
	private static final long serialVersionUID = 3013530531289685076L;

	public ElementAlreadyExistException() {
		super();
	}

	public ElementAlreadyExistException(String message) {
		super(message);
	}

	public ElementAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElementAlreadyExistException(Throwable cause) {
		super(cause);
	}

}

