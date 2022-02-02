package todo.application.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import todo.application.controller.form.MemberArticleForm;
import todo.application.domain.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("[Log-In Check]로그인 검증");

        HttpSession session = request.getSession(false);


        if (session == null || session.getAttribute("loginMember") == null) {
            log.info("[Log-In Check]로그인 실패");
            response.sendRedirect("/");
            return false;
        }


        log.info("[Log-In Check]로그인 성공");
        MemberArticleForm loginMember = (MemberArticleForm) session.getAttribute("loginMember");

        log.info("[LoginMember Id] {}", loginMember.getMemberId());
        log.info("[LoginMember nickname] {}", loginMember.getNickname());
        log.info("[LoginMember joinId] {}", loginMember.getJoinId());

        return true;
    }

}
