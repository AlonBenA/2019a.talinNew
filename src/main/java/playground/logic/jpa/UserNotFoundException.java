package playground.logic.jpa;

public class UserNotFoundException extends Exception {
	private static final long serialVersionUID = 604729857716229974L;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

}
