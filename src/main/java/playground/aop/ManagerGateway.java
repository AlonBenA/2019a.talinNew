package playground.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.Entities.UserEntity;
import playground.logic.Services.PlaygroundUserService;

@Component
@Aspect
public class ManagerGateway {

//	private Log log = LogFactory.getLog(LoggerAspect.class);
	private PlaygroundUserService userService;
	
	@Autowired
	public ManagerGateway(PlaygroundUserService userService) {
		super();
		this.userService = userService;
	}
	
	@Around("@annotation(playground.aop.ManagerCheck) && args(playground, email,..)")
	public Object checkIfPlayer(ProceedingJoinPoint pjp, String playground, String email) throws Throwable {
		UserEntity userEntity = userService.userLogin(playground, email);
		if(!"Manager".equals(userEntity.getRole()))
				throw new RuntimeException("The user is not Manager!");
		
		return pjp.proceed();
	}
}
