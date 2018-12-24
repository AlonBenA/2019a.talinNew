package playground.plugins;

public class Message {
	private String id;
	private String message;
	
	public Message() {
	}
	
	public Message(String message) {
		this.message = message;
	}
	
	public Message(String id, String message) {
		this.message = message;
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
