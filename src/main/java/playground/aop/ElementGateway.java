package playground.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.Services.PlaygroundElementService;
import playground.logic.Services.PlaygroundUserService;
import playground.logic.jpa.ElementNotFoundException;

@Component
@Aspect
public class ElementGateway {
	private PlaygroundElementService Elements;

	@Autowired
	public ElementGateway(PlaygroundElementService Elements) {
		super();
		this.Elements = Elements;
	}
	
	@Around("@annotation(playground.aop.ElementExistCheck) && args(playground, email, ..)")
	public void CheckIfElementExist (ProceedingJoinPoint pjp, String playground, String email) {
		
		
		//not sure we need it... it seem like a code that do the same thing Twice
		
	}
	
	

}
