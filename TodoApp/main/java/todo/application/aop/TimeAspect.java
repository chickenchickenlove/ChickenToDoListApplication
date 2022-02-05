package todo.application.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimeAspect {



    @Around("todo.application.aop.MyPointcut.allControllerServiceRepository()")
    public Object doTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("[{}] Start", joinPoint.getSignature().toShortString());
        long startTime = System.currentTimeMillis();

        // 핵심 로직
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("[{}] end, resultTime = {}", joinPoint.getSignature().toShortString(), resultTime);

        return result;
    }



}
