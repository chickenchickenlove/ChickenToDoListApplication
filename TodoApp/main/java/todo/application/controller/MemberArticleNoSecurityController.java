package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import todo.application.controller.aop.annotation.Retry;
import todo.application.controller.controllerexception.annotation.Monitoring;
import todo.application.controller.form.ArticleForm;
import todo.application.controller.form.MemberLoginSessionForm;
import todo.application.domain.MemberArticle;
import todo.application.service.ArticleService;
import todo.application.service.MemberArticleService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Monitoring
@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberArticleNoSecurityController {


    //== 의존관계==//
    private final ArticleService articleService;
    private final MemberArticleService memberArticleService;




    // 회원 작성 폼으로 넘어감

    @Retry
    @GetMapping("/article/saveform")
    public String articleSaveForm(Model model) {

        ArticleForm articleForm = new ArticleForm();
        model.addAttribute("articleForm", articleForm);

        return "article/articleFormV2";
    }


    @PostMapping("/article/saveForm")
    public String articleSave(@Valid @ModelAttribute(name = "articleForm") ArticleForm form,
                              BindingResult bindingResult, HttpServletRequest request) {

        log.info("Binding Result = {}", bindingResult);

        if (bindingResult.hasErrors()) {
            return "article/articleFormV2";
        }


        // 로그인 처리됨 → Null Check 필요 X

        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute("loginMember");
        articleService.saveNewArticle(form.getWriteContents(), form.getWriteTitle(), form.getDueDate(),loginMember.getMemberId());
        return "redirect:/article/list";
    }

    @Retry
    @GetMapping("article/list")
    public String articleList(HttpServletRequest request, Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {

        log.info("pageable size ={}", pageable.getPageSize());

        // 로그인 정보 확인, 로그인 처리됨 → Null Check 필요 X
        HttpSession session = request.getSession();
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm)session.getAttribute(LoginChar.LOGIN_MEMBER);


        // 로그인 정보 바탕으로 게시글 확인
        Slice<MemberArticle> articleByMemberId = memberArticleService.findPagingArticleByMemberIdNotCompleted(loginMember.getMemberId(), pageable);
        model.addAttribute("memberArticle", articleByMemberId);
        model.addAttribute("memberNickname", loginMember.getNickname());

        return "article/articleListV2";
    }



    @Retry
    @GetMapping("article/list-completed")
    public String articleListCompletedOnly(HttpServletRequest request, Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {

        log.info("pageable size ={}", pageable.getPageSize());

        // 로그인 정보 확인, 로그인 처리됨 → Null Check 필요 X
        HttpSession session = request.getSession();
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm)session.getAttribute(LoginChar.LOGIN_MEMBER);


        // 로그인 정보 바탕으로 게시글 확인
        Slice<MemberArticle> articleByMemberId = memberArticleService.findPagingArticleByMemberIdCompletedOnly(loginMember.getMemberId(), pageable);
        model.addAttribute("memberArticle", articleByMemberId);
        model.addAttribute("memberNickname", loginMember.getNickname());

        return "article/articleListCompleted";
    }




}
