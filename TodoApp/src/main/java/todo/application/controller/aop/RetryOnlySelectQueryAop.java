package todo.application.controller.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import todo.application.controller.aop.annotation.Retry;

import static java.lang.Thread.*;

@Aspect
@Component
@Slf4j
public class RetryOnlySelectQueryAop {

    private Exception exceptionHolder;

    // @Retry 가진 메서드에 적용 → 조회 메서드에 모두 적용 완료
    @Around("@annotation(retry) && execution(* todo.application..*(..))")
    public Object retryAop(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        int limit = retry.count();
        for (int i = 0; i < limit; i++) {
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                log.info("retryAop Start");
                log.info("retry : {} / {}", i, limit);
                exceptionHolder = e;
            }
            sleep(1000);
        }
        throw exceptionHolder;
    }
}
