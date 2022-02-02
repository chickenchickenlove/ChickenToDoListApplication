package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import todo.application.controller.form.ArticleForm;
import todo.application.controller.form.MemberArticleForm;
import todo.application.controller.form.MemberJoinForm;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.domain.QMemberArticle;
import todo.application.service.ArticleService;
import todo.application.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberArticleController {

    private final MemberService memberService;
    private final ArticleService articleService;


    @GetMapping("/article/saveform")
    public String articleSaveForm(Model model) {
        System.out.println("LocalDate.now() = " + LocalDate.now());
        model.addAttribute("articleForm", new ArticleForm());
        return "article/articleForm";
    }


    @PostMapping("/article/saveForm")
    public String articleSave(@ModelAttribute(name = "articleForm") ArticleForm form, Model model,
                              HttpServletRequest request) {

        MemberArticleForm loginMember = (MemberArticleForm) request.getSession().getAttribute("loginMember");


        log.info("article Form localDateTIme= {}", form.getDueDate());



        articleService.saveNewArticle(form.getWriteContents(), form.getWriteTitle(), form.getDueDate(),loginMember.getMemberId());
        return "redirect:/";
    }

    @GetMapping("article/list")
    public String articleList(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        MemberArticleForm loginMember = (MemberArticleForm)session.getAttribute(LoginChar.LOGIN_MEMBER);

        List<MemberArticle> articleByMemberId = articleService.findArticleByMemberId(loginMember.getMemberId());


        model.addAttribute("memberArticle", articleByMemberId);


        return "article/articleList";
    }

    @GetMapping("/article/{articleId}/detail")
    public String articleDetail(@PathVariable(name = "articleId") Long articleId, Model model) {

        Article articleByArticleId = articleService.findArticleByArticleId(articleId);
        model.addAttribute("article", articleByArticleId);

        return "article/articleDetail";
    }



    @GetMapping("/article/{articleId}/edit")
    public String articleEditForm(@PathVariable(name = "articleId") Long articleId, Model model) {
        Article article = articleService.findArticleByArticleId(articleId);
        model.addAttribute("article", article);
        return "article/articleEditForm";
    }


    @PostMapping("/article/{articleId}/edit")
    public String articleEdit(@ModelAttribute(name = "article") Article editArticle , @PathVariable(name = "articleId") Long articleId, Model model) {

        articleService.editNewArticle(articleId, editArticle.getWriteTitle(), editArticle.getWriteContents());
        return "redirect:/";
    }





}

