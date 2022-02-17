package todo.application.controller.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import todo.application.controller.aop.annotation.countannotation.CreateCount;
import todo.application.controller.aop.annotation.countannotation.JoinCount;
import todo.application.controller.aop.annotation.countannotation.ReadCount;
import todo.application.service.VisitorViewService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@RequiredArgsConstructor
@Slf4j
@Component
public class CountAop {

    private final VisitorViewService visitorViewService;

    //createCount가 있으면 write 하나씩 올린다.

    @AfterReturning("@annotation(createCount)")
    public void countCreatedWrite(JoinPoint joinPoint, CreateCount createCount) {

        // Validation Check
        boolean hasBindingResultError = checkBindingResultError(joinPoint);

        // Validation 실패 시, CreatedWrite Count는 증가하지 않는다.
        if (!hasBindingResultError){
            log.info("Write Count add {} -> {}", visitorViewService.getWriteCreatedNumber().get(), visitorViewService.getWriteCreatedNumber().get() + 1L );
            visitorViewService.addWriteCreatedNumber();
        }
    }

    @AfterReturning("@annotation(joinCount)")
    public void countUserJoin(JoinPoint joinPoint, JoinCount joinCount) {

        // Validation Check
        boolean hasBindingResultError = checkBindingResultError(joinPoint);

        // Validation 실패 시, UserJoin Count는 증가하지 않는다.
        if(!hasBindingResultError){
            log.info("UserJoin Count add {} -> {}", visitorViewService.getUserJoin().get(), visitorViewService.getUserJoin().get() + 1L );
            visitorViewService.adduserJoin();
        }
    }


    private boolean checkBindingResultError(JoinPoint joinPoint) {
        boolean hasBindingResultError = false;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult temp = (BindingResult) arg;
                hasBindingResultError  = temp.hasErrors();
            }
        }
        return hasBindingResultError;
    }


}
