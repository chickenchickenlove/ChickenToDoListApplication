package todo.application.controller.controllerexception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import todo.application.controller.controllerexception.annotation.Monitoring;

@Slf4j
@ControllerAdvice(annotations = {Monitoring.class})
public class ErrorControllerAdvice  {


    @ExceptionHandler
    public String illegalExResolver(IllegalStateException e) {
        log.info("ControllerAdvice 실행! ");
        return "article/articleDetailV2";
    }


    @ExceptionHandler
    public String ExResolver(Exception e) {
        log.info("ControllerAdvice 실행!");
        return "error/myError";
    }






}
