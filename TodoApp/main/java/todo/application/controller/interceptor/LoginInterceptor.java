package todo.application.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;
import todo.application.controller.LoginChar;
import todo.application.controller.form.MemberArticleForm;
import todo.application.domain.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static todo.application.controller.LoginChar.*;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {

        String uuid = UUID.randomUUID().toString();
        String requestURI = request.getRequestURI();


        log.info("[{}][Log-In Check]로그인 검증, URI = {}", uuid, requestURI);
        HttpSession session = request.getSession(false);


        // 로그인 실패
        if (session == null || session.getAttribute("loginMember") == null) {
            log.info("[{}][Log-In Check]로그인 실패", uuid);
            response.sendRedirect("/login" + "?requestURI" + "=" + requestURI);
            return false;
        }



        MemberArticleForm loginMembmer = (MemberArticleForm)session.getAttribute(LOGIN_MEMBER);

        // 로그인 성공
        log.info("[{}][Log-In Check]로그인 성공, nickname = {}, userId = {}", uuid, loginMembmer.getNickname(), loginMembmer.getJoinId());
        return true;
    }

}
