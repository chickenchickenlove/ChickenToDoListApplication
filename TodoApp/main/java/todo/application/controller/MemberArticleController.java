package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import todo.application.controller.form.ArticleForm;
import todo.application.controller.form.MemberArticleForm;
import todo.application.controller.form.MemberJoinForm;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.domain.QMemberArticle;
import todo.application.repository.MemberRepository;
import todo.application.repository.MemberSearch;
import todo.application.service.ArticleService;
import todo.application.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static todo.application.controller.LoginChar.LOGIN_MEMBER;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberArticleController {

    private final MemberService memberService;
    private final ArticleService articleService;
    private final MemberRepository memberRepository;


    // 회원 작성 폼으로 넘어감
    @GetMapping("/article/saveform")
    public String articleSaveForm(Model model) {
        ArticleForm articleForm = new ArticleForm();
        model.addAttribute("articleForm", articleForm);
        return "article/articleFormV2";
    }


    @PostMapping("/article/saveForm")
    public String articleSave(@Valid @ModelAttribute(name = "articleForm") ArticleForm form,
                              BindingResult bindingResult, Model model,HttpServletRequest request) {

        log.info("Binding Result = {}", bindingResult);

        if (bindingResult.hasErrors()) {
            return "article/articleFormV2";
        }


        MemberArticleForm loginMember = (MemberArticleForm) request.getSession().getAttribute("loginMember");
        log.info("article Form localDateTIme= {}", form.getDueDate());
        articleService.saveNewArticle(form.getWriteContents(), form.getWriteTitle(), form.getDueDate(),loginMember.getMemberId());
        return "redirect:/article/list";
    }

    @GetMapping("article/list")
    public String articleList(HttpServletRequest request, Model model) {

        // 로그인 정보 확인
        HttpSession session = request.getSession();
        MemberArticleForm loginMember = (MemberArticleForm)session.getAttribute(LoginChar.LOGIN_MEMBER);


        // 로그인 정보 바탕으로 게시글 확인
        List<MemberArticle> articleByMemberId = articleService.findArticleByMemberId(loginMember.getMemberId());

        for (MemberArticle memberArticle : articleByMemberId) {
            log.info("memberArticle : {}", memberArticle.getArticle().getStatus());
        }


        model.addAttribute("memberArticle", articleByMemberId);
        model.addAttribute("memberNickname", loginMember.getNickname());

        return "article/articleListV2";
    }

    @GetMapping("/article/{articleId}/detail")
    public String articleDetail(@PathVariable(name = "articleId") Long articleId, Model model) {

        Article articleByArticleId = articleService.findArticleByArticleId(articleId);
        model.addAttribute("article", articleByArticleId);

        return "article/articleDetailV2";
    }


    @GetMapping("/article/{articleId}/edit")
    public String articleEditForm(@PathVariable(name = "articleId") Long articleId, Model model) {
        Article article = articleService.findArticleByArticleId(articleId);
        model.addAttribute("article", article);
        return "article/articleEditFormV2";
    }


    @PostMapping("/article/{articleId}/edit")
    public String articleEdit(@Valid @ModelAttribute(name = "article") Article editArticle , BindingResult bindingResult,
                              @PathVariable(name = "articleId") Long articleId, HttpServletRequest request) {


        if(editArticle.getStatus() == null){
            bindingResult.reject("StatusError", "Choose Status는 선택할 수 없어요!");
        }



        if (bindingResult.hasErrors()) {
            return "article/articleEditFormV2";
        }

        Long loginMemberId = getLoginMemberId(request);
        articleService.editNewArticle(articleId, editArticle.getStatus(),editArticle.getWriteTitle(), editArticle.getWriteContents(), loginMemberId);
        return "redirect:/";
    }



    @GetMapping("/article/{articleId}/share")
    public String articleEditShare(@ModelAttribute(name = "memberSearch") MemberSearch memberSearch,
            @PathVariable(name = "articleId") Long articleId, Model model) {

        // 회원 정보를 불러와서 모델에 넣어준다
        // 회원 정보가 출력됨
        log.info("memberSearch = {}", memberSearch);
        List<Member> members = memberService.findMemberByMemberSearch(memberSearch);

        model.addAttribute("members", members);
        return "article/articleShareV2";
    }


    @GetMapping("/article/share/do")
    public String articleShareDoing(@RequestParam(name = "memberId") Long memberId,
                                   @RequestParam(name = "articleId") Long articleId,
                                    HttpServletRequest request) {


        Long loginMemberId = getLoginMemberId(request);

        articleService.shareArticleWithOthers(memberId, articleId,loginMemberId);
        return "redirect:/";
    }


    @GetMapping("/article/{articleId}/complete")
    public String articleDoComplete(@PathVariable(name = "articleId") Long articleId,
                                    HttpServletRequest request){

        Long loginMemberId = getLoginMemberId(request);
        articleService.completeArticle(articleId, loginMemberId);
        return "redirect:/";
    }




    @GetMapping("/article/{articleId}/remove")
    public String articleDoDelete(@PathVariable(name = "articleId") Long articleId,
                                  HttpServletRequest request){

        Long loginMemberId = getLoginMemberId(request);


        articleService.deleteArticle(articleId, loginMemberId);
        return "redirect:/";
    }


    //== 로그인 ID 찾기==//

    private Long getLoginMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MemberArticleForm loginMember = (MemberArticleForm) session.getAttribute(LOGIN_MEMBER);
        Long memberId = loginMember.getMemberId();
        return memberId;
    }


}

