package playground.layout;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import playground.logic.Location;
import playground.logic.Entities.ElementEntity;

public class ElementTO {

	
	private String playground; 
	private String id;
	private Location location;
	private String name;
	private Date creationDate;
	private Date exirationDate;
	private String type;
	private Map<String,Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;
	
	
	public ElementTO() {
		super();
		this.playground = "2019a.talin";
		this.id = "0";
		this.location = new Location(0, 0);
		this.name = "Animal";
		this.creationDate = new Date();
		this.exirationDate = null;
		this.type = "Animal";
		this.attributes = new HashMap<>();
		this.creatorPlayground = "2019a.talin";
		this.creatorEmail = "2019a.Talin@Mail.com";
	}

	
	public ElementTO(Location location,String name,Date exirationDate,String type
			,Map<String,Object> attributes,String creatorPlayground, String creatorEmail)
	{
		this.playground = "2019a.talin";
		this.id = "0";
		setLocation(location);
		setName(name);
		this.creationDate = new Date();
		setExirationDate(exirationDate);
		setType(type);
		setAttributes(attributes);
		setCreatorPlayground(creatorPlayground);
		setCreatorEmail(creatorEmail);
	}
	
	public ElementTO(ElementEntity elementEntity)
	{
		this.playground = elementEntity.getPlayground();
		this.id = elementEntity.getId();
		this.location = elementEntity.getLocation();
		this.name = elementEntity.getName();
		this.creationDate = elementEntity.getCreationDate();
		this.exirationDate = elementEntity.getExirationDate();
		this.type = elementEntity.getType();
		this.attributes = elementEntity.getAttributes();
		this.creatorPlayground = elementEntity.getCreatorPlayground();
		this.creatorEmail = elementEntity.getCreatorEmail();
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
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
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
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
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
	
	
	public ElementEntity convertFromElementTOToElementEntity()
	{

		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setId(id);
		elementEntity.setAttributes(attributes);
		elementEntity.setCreationDate(creationDate);
		elementEntity.setCreatorEmail(creatorEmail);
		elementEntity.setCreatorPlayground(creatorPlayground);
		elementEntity.setExirationDate(exirationDate);
		elementEntity.setLocation(location);
		elementEntity.setName(name);
		elementEntity.setPlayground(creatorPlayground);
		elementEntity.setType(type);	
		
		return elementEntity;
	}

	@Override
	public String toString() {
		return "ElementTO [playground=" + playground + ", id=" + id + ", location=" + location + ", name=" + name
				+ ", creationDate=" + creationDate + ", exirationDate=" + exirationDate + ", type=" + type
				+ ", attributes=" + attributes + ", creatorPlayground=" + creatorPlayground + ", creatorEmail="
				+ creatorEmail + "]";
	}	
	
}
