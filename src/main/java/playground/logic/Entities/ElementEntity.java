package playground.logic.Entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import com.fasterxml.jackson.databind.ObjectMapper;


import playground.logic.Location;


@Entity
@Table(name = "Elements")
public class ElementEntity {


	private String playground; 
	private String id;
	private String name;
	private Date creationDate;
	private Date exirationDate;
	private String type;
	private Map<String,Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;
	private Double x;
	private Double y;
	
	
	public ElementEntity() {
		super();
		this.playground = "2019a.talin";
		this.id = "0";
		this.x = 0.0;
		this.y = 0.0;
		this.name = "Animal";
		this.creationDate = new Date();
		this.exirationDate = null;
		this.type = "Animal";
		this.attributes = new HashMap<>();
		this.creatorPlayground = "2019a.talin";
		this.creatorEmail = "2019a.Talin@Mail.com";
	}
	
	public ElementEntity(Location location,String name,Date exirationDate,String type
			,Map<String,Object> attributes,String creatorPlayground, String creatorEmail)
	{
		this.playground = "2019a.talin";
		this.id = "0";
		//setLocation(location);
		this.x = location.getX();
		this.y = location.getY();
		setName(name);
		this.creationDate = new Date();
		setExirationDate(exirationDate);
		setType(type);
		setAttributes(attributes);
		setCreatorPlayground(creatorPlayground);
		setCreatorEmail(creatorEmail);
	}
	
	
	
	@Id
	public String getKey() {
		return playground + "@@" + id;
	}
	

	public void setKey(String key) {
		
		String[] split = key.split("@@");
		setPlayground(split[0]);
		setId(split[1]);
		
	}
	
	@Transient
	public String getPlayground() {
		return playground;
	}
	public void setPlayground(String playground) {
		this.playground = playground;
	}
	
	@Transient
	public String getId() {
		return id;
	}
	@Transient
	public void setId(String id) {
		this.id = id;
	}
	
	
	
	/*
	@Transient
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@Lob
	public String getLocationJson() {
		try {
			return new ObjectMapper().writeValueAsString(this.location);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void setLocationJson(String Locationjson) {
		try {
			ObjectMapper OM = new ObjectMapper();
			this.location = OM.readValue(Locationjson, Location.class);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	*/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getExirationDate() {
		return exirationDate;
	}
	public void setExirationDate(Date exirationDate) {
		this.exirationDate = exirationDate;
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	@Transient
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Lob
	public String getAttributesJson() {
		try {
			return new ObjectMapper().writeValueAsString(this.attributes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void setAttributesJson(String json) {
		try {
			this.attributes = new ObjectMapper().readValue(json, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public String getCreatorPlayground() {
		return creatorPlayground;
	}
	public void setCreatorPlayground(String creatorPlayground) {
		this.creatorPlayground = creatorPlayground;
	}
	public String getCreatorEmail() {
		return creatorEmail;
	}
	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((creatorEmail == null) ? 0 : creatorEmail.hashCode());
		result = prime * result + ((creatorPlayground == null) ? 0 : creatorPlayground.hashCode());
		result = prime * result + ((exirationDate == null) ? 0 : exirationDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((playground == null) ? 0 : playground.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
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
		ElementEntity other = (ElementEntity) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (creatorEmail == null) {
			if (other.creatorEmail != null)
				return false;
		} else if (!creatorEmail.equals(other.creatorEmail))
			return false;
		if (creatorPlayground == null) {
			if (other.creatorPlayground != null)
				return false;
		} else if (!creatorPlayground.equals(other.creatorPlayground))
			return false;
		if (exirationDate == null) {
			if (other.exirationDate != null)
				return false;
		} else if (!exirationDate.equals(other.exirationDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ElementEntity [playground=" + playground + ", id=" + id + ", name=" + name + ", creationDate="
				+ creationDate + ", exirationDate=" + exirationDate + ", type=" + type + ", attributes=" + attributes
				+ ", creatorPlayground=" + creatorPlayground + ", creatorEmail=" + creatorEmail + ", x=" + x + ", y="
				+ y + "]";
	}


	
	
	
	
	
	
}
