package todo.application.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import todo.application.controller.form.MemberLoginSessionForm;
import todo.application.domain.MemberGrade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static todo.application.controller.LoginChar.LOGIN_MEMBER;

@Slf4j
public class AdminLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {

        String uuid = UUID.randomUUID().toString();
        String requestURI = request.getRequestURI();


        log.info("[{}][Admin Log-In Check]로그인 검증, URI = {}", uuid, requestURI);
        HttpSession session = request.getSession(false);


        // 로그인 실패
        if (session == null || session.getAttribute("loginMember") == null) {
            log.info("[{}][Admin Log-In Check]로그인 실패", uuid);
            response.sendRedirect("/");
            return false;
        }

        MemberLoginSessionForm loginMember = (MemberLoginSessionForm)session.getAttribute(LOGIN_MEMBER);
        if (loginMember.getMemberGrade().equals(MemberGrade.NORMAL)) {
            log.info("[{}][Admin Log-In Check]로그인 실패, nickname = {}, userId = {}", uuid, loginMember.getNickname(), loginMember.getJoinId());
            response.sendRedirect("/");
            return false;
        }

        // 로그인 성공
        log.info("[{}][Admin Log-In Check]로그인 성공, nickname = {}, userId = {}", uuid, loginMember.getNickname(), loginMember.getJoinId());
        return true;
    }

}
