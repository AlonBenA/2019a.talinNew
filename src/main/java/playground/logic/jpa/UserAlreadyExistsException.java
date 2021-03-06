package playground.logic.jpa;

public class UserAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 874896183523912962L;
	
	public UserAlreadyExistsException() {
		super();
	}

	public UserAlreadyExistsException(String message) {
		super(message);
	}

	public UserAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserAlreadyExistsException(Throwable cause) {
		super(cause);
	}
}
