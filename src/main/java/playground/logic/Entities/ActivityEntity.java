package playground.logic.Entities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;

public class ActivityEntity {
	
	private static AtomicLong IDGiver = new AtomicLong(0);
	private String playground;
	private String id;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private Map<String,Object> attributes;
	
	public ActivityEntity()
	{
		super();
		this.playground = "2019a.talin";
		this.id = getID();
		this.elementPlayground = "2019a.talin";
		this.elementId = "0";
		this.type = "feed";
		this.playerPlayground = "2019a.talin";
		this.playerEmail = "email@gmail.com";
		this.attributes = new HashMap<>();
		this.attributes.put("eat", "meat");
	}
	
	public ActivityEntity(String elementPlayground, String elementId, String type,
			String playerPlayground, String playerEmail, Map<String, Object> attributes) {
		super();
		this.playground = "2019a.talin";
		this.id = getID();
		setElementPlayground(elementPlayground);
		setElementId(elementId);
		setType(type);
		setPlayerPlayground(playerPlayground);
		setPlayerEmail(playerEmail);
		setAttributes(attributes);
	}
	
	private String getID()
	{
		return IDGiver.getAndIncrement() +"";
	}
	
	public String getPlayground() {
		return playground;
	}
	
	public void setPlayground(String playground) {
		this.playground = playground;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getElementPlayground() {
		return elementPlayground;
	}
	public void setElementPlayground(String elementPlayground) {
		this.elementPlayground = elementPlayground;
	}
	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPlayerPlayground() {
		return playerPlayground;
	}
	public void setPlayerPlayground(String playerPlayground) {
		this.playerPlayground = playerPlayground;
	}
	public String getPlayerEmail() {
		return playerEmail;
	}
	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "ActivityEntity [playground=" + playground + ", id=" + id + ", elementPlayground=" + elementPlayground
				+ ", elementId=" + elementId + ", type=" + type + ", playerPlayground=" + playerPlayground
				+ ", playerEmail=" + playerEmail + ", attributes=" + attributes + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result + ((elementPlayground == null) ? 0 : elementPlayground.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((playerEmail == null) ? 0 : playerEmail.hashCode());
		result = prime * result + ((playerPlayground == null) ? 0 : playerPlayground.hashCode());
		result = prime * result + ((playground == null) ? 0 : playground.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ActivityEntity other = (ActivityEntity) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (elementPlayground == null) {
			if (other.elementPlayground != null)
				return false;
		} else if (!elementPlayground.equals(other.elementPlayground))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (playerEmail == null) {
			if (other.playerEmail != null)
				return false;
		} else if (!playerEmail.equals(other.playerEmail))
			return false;
		if (playerPlayground == null) {
			if (other.playerPlayground != null)
				return false;
		} else if (!playerPlayground.equals(other.playerPlayground))
			return false;
		if (playground == null) {
			if (other.playground != null)
				return false;
		} else if (!playground.equals(other.playground))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
