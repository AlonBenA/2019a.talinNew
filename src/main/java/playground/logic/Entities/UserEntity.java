package playground.logic.Entities;

import java.util.Random;

public class UserEntity {
	private String email;
	private String playground;
	private String username;
	private String avatar;
	private String role;
	private long points;
	private StringBuilder code;

	public UserEntity() {
		this("","","","");
	}
	
	public UserEntity(String email, String username, String avatar, String role) {
		super();
		setEmail(email);
		this.playground = "2019a.talin";
		setUsername(username);
		setAvatar(avatar);
		setRole(role);
		this.points = 0;
//		this.code.append(1234);
		Random r = new Random();
		int low = 1000;
		int high = 9999;
		int result = r.nextInt(high-low) + low;
		code = new StringBuilder(result);
		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
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

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	public String getCode() {
		return code.toString();
	}
	
	public void setCode(String code) {
		this.code.delete(0, this.code.length());
		this.code.append(code);
	}
	
	public boolean verify(String code) {
		if(code.equals(this.code.toString())) {
			this.code = null;
//			this.code.delete(0, this.code.length());
			return true;
		}
		return false;
	}
	
	public boolean isVerified() {
//		if("".equals(code.toString()))
		if(code == null)
			return true;
		return false;
	}
/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((playground == null) ? 0 : playground.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEntity other = (UserEntity) obj;
				
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		
		if (playground == null) {
			if (other.playground != null)
				return false;
		} else if (!playground.equals(other.playground))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
				
		if (avatar == null) {
			if (other.avatar != null)
				return false;
		} else if (!avatar.equals(other.avatar))
			return false;
		
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
			
		if (!(points == other.points))
			return false;
				
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		
		return true;
	}
	
*/
}
