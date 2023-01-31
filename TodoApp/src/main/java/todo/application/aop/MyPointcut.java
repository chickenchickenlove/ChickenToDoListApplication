package todo.application.aop;


import org.aspectj.lang.annotation.Pointcut;

public class MyPointcut {

    @Pointcut("execution(* todo.application.controller..*(..))")
    public void allController(){}

    @Pointcut("execution(* todo.application.repository..*(..))")
    public void allRepository(){}

    @Pointcut("execution(* todo.application.service..*(..))")
    public void allService(){}

    @Pointcut("allService() || allController() || allRepository()")
    public void allControllerServiceRepository(){}



}
