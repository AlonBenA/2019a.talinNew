package playground.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerAspect {
	private Log log = LogFactory.getLog(LoggerAspect.class);

	@Around("@annotation(playground.aop.MyLogger)")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		// bofore
		String className = pjp.getTarget().getClass().getSimpleName();
		String methodName = pjp.getSignature().getName();
		String logOutput = className + "." + methodName + "()";
		log.info(logOutput + " - start");

		try {
			Object rv = pjp.proceed();
			// after success
			log.info(logOutput + " - end successfully");

			return rv;
		} catch (Throwable e) {
			// after failure
			log.info(logOutput + " - end with errors: " + e.getClass().getName());

			throw e;
		}

	}

}
