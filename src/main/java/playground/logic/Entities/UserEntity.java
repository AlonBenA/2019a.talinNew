package playground.logic.Entities;

import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "USER")
public class UserEntity {
	private String email;
	private String playground;
	private String username;
	private String avatar;
	private String role;
	private Long points;
	private StringBuilder code;

	public UserEntity() {
		this("", "", "", "");
	}

	public UserEntity(String email, String username, String avatar, String role) {
		super();
		setEmail(email);
		this.playground = "2019a.talin";
		setUsername(username);
		setAvatar(avatar);
		setRole(role);
		this.points = 0L;
		Random r = new Random();
		int low = 1000;
		int high = 9999;
		int result = r.nextInt(high - low) + low;
		code = new StringBuilder();
		code.append(result);
	}

	@Id
	public String getKey() {
		return playground + "@@" + email;
	}

	public void setKey(String key) {
		String[] tmp = key.split("@@");
		this.playground = tmp[0];
		this.email = tmp[1];
	}

//	public void setKey(String key) {}

	@Transient
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Transient
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

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
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
		if (code.equals(this.code.toString())) {
			this.code = null;
//			this.code.delete(0, this.code.length());
			return true;
		}
		return false;
	}

	@Transient
	public boolean isVerified() {
//		if("".equals(code.toString()))
		if (code == null)
			return true;
		return false;
	}

}
