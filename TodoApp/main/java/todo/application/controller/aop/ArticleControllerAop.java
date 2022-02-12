package todo.application.controller.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import todo.application.controller.aop.annotation.EditDetailSecurity;
import todo.application.controller.aop.annotation.MySecurity;
import todo.application.controller.aop.annotation.ShareSecurity;
import todo.application.controller.controllerexception.annotation.Monitoring;
import todo.application.controller.form.MemberLoginSessionForm;
import todo.application.controller.form.ShareForm;
import todo.application.domain.MemberArticle;
import todo.application.domain.RequestShareArticle;
import todo.application.service.MemberArticleService;
import todo.application.service.RequestShareArticleService;

import javax.servlet.http.HttpServletRequest;

import static todo.application.controller.LoginChar.LOGIN_MEMBER;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ArticleControllerAop {

    private final MemberArticleService memberArticleService;
    private final RequestShareArticleService requestShareArticleService;

    // EDIT / DETAIL / CANCEL / LIST 형태에만 적용
    @Around("@annotation(mySecurity) && args(.., articleId, request) && @annotation(editDetailSecurity)")
    public Object editDetailSecurityLog(ProceedingJoinPoint joinPoint, EditDetailSecurity editDetailSecurity, MySecurity mySecurity, HttpServletRequest request, Long articleId) throws Throwable {
        log.info("[EDIT / DETAIL / CANCEL / List Security AOP Start]");

        // 로그인 정보
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute(LOGIN_MEMBER);
        MemberArticle memberArticle = memberArticleService.findMemberArticleByMemberIdAndArticleId(loginMember.getMemberId(), articleId);

        // 성공 로직
        if (memberArticle != null) {
            log.info("이 회원은 이 게시글에 접근할 수 있습니다.");
            return joinPoint.proceed();
        }

        // 실패 시 Exception
        throw new IllegalArgumentException("잘못된 접근입니다");
    }


    // Form을 가진 Share 메서드에만 적용
    @Around("@annotation(mySecurity) && args(form ,request, ..) && @annotation(shareSecurity)")
    public Object shareSecurityLog(ProceedingJoinPoint joinPoint, MySecurity mySecurity, ShareSecurity shareSecurity,
                                   ShareForm form, HttpServletRequest request) throws Throwable {
        log.info("[Share Security AOP Start with Form]");


        // 로그인 정보 조회
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute(LOGIN_MEMBER);
        MemberArticle memberArticle = memberArticleService.findMemberArticleByMemberIdAndArticleId(loginMember.getMemberId(), form.getArticleId());

        // 성공 로직
        // 1. 로그인 멤버가 공유할 Article을 가지고 있다
        if (memberArticle != null) {
            // 2. 공유할 Article의 작성자가 로그인 멤버다 → 성공
            if (memberArticle.getArticle().getWriter().equals(loginMember.getNickname())) {
                log.info("이 회원은 이 게시글을 공유할 수 있습니다.");
                return joinPoint.proceed();
            }
        }

        //실패 시 Exception 발생
        log.info("이 회원은 이 게시글을 공유할 수 없습니다.");
        throw new IllegalArgumentException("게시글을 공유할 수 없습니다");
    }


    // PathVariable을 URL로 접근하는 Share 메서드에만 적용
    @Around("@annotation(mySecurity) && args(requestShareArticleId, request) && @annotation(shareSecurity)")
    public Object shareSecurityLog2(ProceedingJoinPoint joinPoint, MySecurity mySecurity, ShareSecurity shareSecurity,
                                   Long requestShareArticleId, HttpServletRequest request) throws Throwable {
        log.info("[Share Security AOP Start with PathVariable]");

        // 로그인 정보 및 관련 정보 확인
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute(LOGIN_MEMBER);
        RequestShareArticle result = requestShareArticleService.findRequestShareArticleByMemberIdRequestShareArticleId(loginMember.getMemberId(), requestShareArticleId);

        // 성공 로직
        if (result != null) {
            log.info("이 회원은 이 게시글을 공유할 수 있습니다.");
            return joinPoint.proceed();
        }


        // 실패 로직 --> 예외 발생
        throw new IllegalArgumentException("게시글을 공유할 수 없습니다");
    }



}
