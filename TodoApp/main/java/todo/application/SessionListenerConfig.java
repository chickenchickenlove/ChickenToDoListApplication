package todo.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import todo.application.service.VisitorViewService;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionListenerConfig implements HttpSessionListener {

    private final VisitorViewService visitorViewService;
    private final ConcurrentHashMap<String, String> sessionStore = new ConcurrentHashMap<>();


    @Override
    public void sessionCreated(HttpSessionEvent se) {

        String sessionId = se.getSession().getId();
        if (sessionStore.get(sessionId) != null) {
            return;
        }
        sessionStore.put(sessionId, "ok");
        visitorViewService.addView();
        log.info("Today Visitor : {}", visitorViewService.getView());
    }


    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        sessionStore.remove(sessionId);
    }
}
