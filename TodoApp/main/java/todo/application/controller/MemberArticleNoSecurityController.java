package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import todo.application.controller.form.ArticleForm;
import todo.application.controller.form.MemberArticleForm;
import todo.application.controller.form.MemberLoginSessionForm;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberSearch;
import todo.application.service.ArticleService;
import todo.application.service.MemberArticleService;
import todo.application.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

import static todo.application.controller.LoginChar.LOGIN_MEMBER;


@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberArticleNoSecurityController {



    private final MemberService memberService;
    private final ArticleService articleService;
    private final MemberArticleService memberArticleService;


    // 회원 작성 폼으로 넘어감
    @GetMapping("/article/saveform")
    public String articleSaveForm(Model model) {
        ArticleForm articleForm = new ArticleForm();
        model.addAttribute("articleForm", articleForm);
        return "article/articleFormV2";
    }


    @PostMapping("/article/saveForm")
    public String articleSave(@Valid @ModelAttribute(name = "articleForm") ArticleForm form,
                              BindingResult bindingResult, Model model, HttpServletRequest request) {

        log.info("Binding Result = {}", bindingResult);

        if (bindingResult.hasErrors()) {
            return "article/articleFormV2";
        }


        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute("loginMember");
        log.info("article Form localDateTIme= {}", form.getDueDate());
        articleService.saveNewArticle(form.getWriteContents(), form.getWriteTitle(), form.getDueDate(),loginMember.getMemberId());
        return "redirect:/article/list";
    }

    @GetMapping("article/list")
    public String articleList(HttpServletRequest request, Model model) {

        // 로그인 정보 확인
        HttpSession session = request.getSession();
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm)session.getAttribute(LoginChar.LOGIN_MEMBER);


        // 로그인 정보 바탕으로 게시글 확인
        List<MemberArticle> articleByMemberId = articleService.findArticleByMemberId(loginMember.getMemberId());

        for (MemberArticle memberArticle : articleByMemberId) {
            log.info("memberArticle : {}", memberArticle.getArticle().getStatus());
        }


        model.addAttribute("memberArticle", articleByMemberId);
        model.addAttribute("memberNickname", loginMember.getNickname());

        return "article/articleListV2";
    }





}
