package playground.jpadal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class GeneratedNumber {
	private Long nextValue;
	
	public GeneratedNumber() {
	}

	@Id
	@GeneratedValue
	public Long getNextValue() {
		return nextValue;
	}

	public void setNextValue(Long nextValue) {
		this.nextValue = nextValue;
	}
	
	
}