package playground.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Services.PlaygroundElementService;

@Component
@Aspect
public class ElementGateway {
	private PlaygroundElementService Elements;

	@Autowired
	public ElementGateway(PlaygroundElementService Elements) {
		super();
		this.Elements = Elements;
	}
	
	@Around("@annotation(playground.aop.ElementExistCheck) && args(playground, email, activityEntity,..)")
	public void CheckIfElementExist (ProceedingJoinPoint pjp, String playground, String email,ActivityEntity activityEntity)  throws Throwable  {
		
		Elements.getElement(playground, email, activityEntity.getElementId(), activityEntity.getElementPlayground());
		
	}
	
	

}
