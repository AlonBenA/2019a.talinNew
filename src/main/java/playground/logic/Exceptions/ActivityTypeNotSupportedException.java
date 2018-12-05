package playground.logic.Exceptions;

public class ActivityTypeNotSupportedException extends RuntimeException {
	private static final long serialVersionUID = -3727493402514143970L;

	public ActivityTypeNotSupportedException() {
		super();
	}

	public ActivityTypeNotSupportedException(String message) {
		super(message);
	}

	public ActivityTypeNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActivityTypeNotSupportedException(Throwable cause) {
		super(cause);
	}

}
