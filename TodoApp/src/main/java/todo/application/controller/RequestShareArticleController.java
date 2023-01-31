package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import todo.application.controller.aop.annotation.MySecurity;
import todo.application.controller.aop.annotation.Retry;
import todo.application.controller.aop.annotation.ShareSecurity;
import todo.application.controller.form.MemberLoginSessionForm;
import todo.application.domain.RequestShareArticle;
import todo.application.service.ArticleService;
import todo.application.service.RequestShareArticleService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static todo.application.controller.LoginChar.LOGIN_MEMBER;

@Controller
@RequestMapping("/share")
@Slf4j
@RequiredArgsConstructor
public class RequestShareArticleController {

    private final RequestShareArticleService requestShareArticleService;
    private final ArticleService articleService;

    // 인터셉터로 보안처리 됨.
    @GetMapping("/list")
    @Retry
    public String shareList(HttpServletRequest request, Model model, @PageableDefault(page = 0 ,size = 10) Pageable pageable) {


        // 값 찾음 → Model에 넣음.
        Long loginMemberId = getLoginMemberId(request);
        Slice<RequestShareArticle> requestShareArticle = requestShareArticleService.findSliceRequestShareArticle(loginMemberId, pageable);
        model.addAttribute("shareArticle", requestShareArticle);

        // 뷰 랜더링
        return "share/shareList";
    }


    // 정상 동작 확인
    @MySecurity @ShareSecurity
    @GetMapping("/{requestShareArticleId}")
    public String doShareRequestShareArticle(@PathVariable(name = "requestShareArticleId") Long requestShareArticleId,
                                             HttpServletRequest request) {

        log.info("doShareRequestShareArticle call!");

        RequestShareArticle requestShareArticleById = requestShareArticleService.findRequestShareArticleById(requestShareArticleId);
        articleService.shareArticleWithOthers(requestShareArticleById.getToMember().getId(),requestShareArticleById.getArticle().getId(),requestShareArticleById.getFromMemberId());
        requestShareArticleService.removeRequestShareArticle(requestShareArticleId);

        return "redirect:/";
    }

    // 정상 동작 확인
    @MySecurity @ShareSecurity
    @GetMapping("/{requestShareArticleId}/cancel")
    public String doCancelRequestShareArticle(@PathVariable(name = "requestShareArticleId") Long requestShareArticleId,
                                              HttpServletRequest request) {
        requestShareArticleService.removeRequestShareArticle(requestShareArticleId);
        return "redirect:/";
    }



    // == Validation 로직 ==//
    private Long getLoginMemberId(HttpServletRequest request) {

        // 로그인 상태 보장됨(Interceptor → null 체크 필요 X)
        HttpSession session = request.getSession(false);
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) session.getAttribute(LOGIN_MEMBER);
        return loginMember.getMemberId();

    }




}
