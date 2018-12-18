package playground.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.Services.PlaygroundUserService;

@Component
@Aspect
public class UserVerifiedAndExistGateway {
//	private Log log = LogFactory.getLog(LoggerAspect.class);
	private PlaygroundUserService userService;
	
	@Autowired
	public UserVerifiedAndExistGateway(PlaygroundUserService userService) {
		super();
		this.userService = userService;
	}
	
	@Around("@annotation(playground.aop.UserVerifiedAndExistCheck) && args(playground, email, ..)")
	public Object checkIfUserExist(ProceedingJoinPoint pjp, String playground, String email) throws Throwable {
		userService.userLogin(playground, email);
		return pjp.proceed();
	}
	
}
