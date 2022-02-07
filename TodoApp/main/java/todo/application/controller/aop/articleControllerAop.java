package todo.application.controller.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import todo.application.controller.aop.annotation.MySecurity;
import todo.application.controller.form.MemberLoginSessionForm;
import todo.application.controller.form.ShareForm;
import todo.application.domain.MemberArticle;
import todo.application.service.MemberArticleService;

import javax.servlet.http.HttpServletRequest;

import static todo.application.controller.LoginChar.LOGIN_MEMBER;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class articleControllerAop {

    private final MemberArticleService memberArticleService;

    @Around("@annotation(mySecurity) && args(.., articleId, request)")
    public Object editDetailSecurityLog(ProceedingJoinPoint joinPoint, MySecurity mySecurity, HttpServletRequest request, Long articleId) throws Throwable {
        log.info("[EDIT / DETAIL Security Log Start]");

        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute(LOGIN_MEMBER);
        MemberArticle memberArticle = memberArticleService.findMemberArticleByMemberIdAndArticleId(loginMember.getMemberId(), articleId);

        if (memberArticle != null) {
            log.info("이 회원은 이 게시글에 접근할 수 있습니다.");
            return joinPoint.proceed();
        }

        throw new IllegalArgumentException("잘못된 접근입니다");
    }


    @Around("@annotation(mySecurity) && args(form ,request)")
    public Object shareSecurityLog(ProceedingJoinPoint joinPoint, MySecurity mySecurity,
                                   ShareForm form, HttpServletRequest request) throws Throwable {
        log.info("[Share Security Log Start]");

        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute(LOGIN_MEMBER);
        MemberArticle memberArticle = memberArticleService.findMemberArticleByMemberIdAndArticleId(loginMember.getMemberId(), form.getArticleId());

        // 1. 로그인 멤버가 공유할 Article을 가지고 있다
        if (memberArticle != null) {
            // 2. 공유할 Article의 작성자가 로그인 멤버다 → 성공
            if (memberArticle.getArticle().getWriter().equals(loginMember.getNickname())) {
                log.info("이 회원은 이 게시글을 공유할 수 있습니다.");
                return joinPoint.proceed();
            }
        }
        throw new IllegalArgumentException("게시글을 공유할 수 없습니다");
    }
}
