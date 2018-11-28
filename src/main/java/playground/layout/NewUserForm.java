package playground.layout;

public class NewUserForm {
	private String email;
	private String username;
	private String avatar;
	private String role;
	
	public NewUserForm() {
		this("","","","");
	}

	public NewUserForm(String email, String username, String avatar, String role) {
		super();
		setEmail(email);
		setUsername(username);
		setAvatar(avatar);
		setRole(role);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	
}
