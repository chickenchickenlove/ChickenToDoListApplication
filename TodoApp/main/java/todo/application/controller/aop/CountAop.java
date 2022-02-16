package todo.application.controller.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import todo.application.controller.aop.annotation.countannotation.CreateCount;
import todo.application.controller.aop.annotation.countannotation.JoinCount;
import todo.application.controller.aop.annotation.countannotation.ReadCount;
import todo.application.service.VisitorViewService;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@RequiredArgsConstructor
@Slf4j
@Component
public class CountAop {

    private final VisitorViewService visitorViewService;

    //createCount가 있으면 write 하나씩 올린다.

    @AfterReturning("@annotation(createCount)")
    public void countCreatedWrite(CreateCount createCount) {
        log.info("Write Count add {} -> {}", visitorViewService.getWriteCreatedNumber().get(), visitorViewService.getWriteCreatedNumber().get() + 1L );
        visitorViewService.addWriteCreatedNumber();
    }


    @AfterReturning("@annotation(joinCount)")
    public void countUserJoin(JoinCount joinCount) {
        log.info("UserJoin Count add {} -> {}", visitorViewService.getUserJoin().get(), visitorViewService.getUserJoin().get() + 1L );
        visitorViewService.adduserJoin();
    }

    @AfterReturning("@annotation(readCount) && args(request)")
    public void countView(HttpServletRequest request, ReadCount readCount) {
        String clientIP = getClientIP(request);




    }




    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        log.info("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info("> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.info(">  WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            log.info("> getRemoteAddr : "+ip);
        }
        log.info("> Result : IP Address : "+ip);

        return ip;
    }








}
