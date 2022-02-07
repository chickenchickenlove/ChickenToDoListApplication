package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import todo.application.controller.aop.annotation.MySecurity;
import todo.application.controller.form.MemberLoginSessionForm;
import todo.application.controller.form.ShareForm;
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
public class MemberArticleSecurityController {


    private final MemberService memberService;
    private final ArticleService articleService;
    private final MemberArticleService memberArticleService;


    @ExceptionHandler
    public Object exception(HttpServletRequest request, IllegalArgumentException e) {

        if (e.getMessage().equals("잘못된 접근입니다")) {
            // 접근 권한 없는 행위 → 초기 화면으로 돌아감
            return new ModelAndView("redirect:/article/list");
        }

        return new ModelAndView();
    }


    @MySecurity
    @GetMapping("/article/{articleId}/detail")
    public String articleDetail(Model model, @PathVariable(name = "articleId") Long articleId,  HttpServletRequest request) {

        MemberArticle isReadPossibleThisArticleResult = isReadOrEditPossibleThisArticle(request, articleId);
        Article article = isReadPossibleThisArticleResult.getArticle();
        model.addAttribute("article", article);

        return "article/articleDetailV2";
    }


    @MySecurity
    @GetMapping("/article/{articleId}/edit")
    public String articleEditForm(Model model, @PathVariable(name = "articleId") Long articleId, HttpServletRequest request) {

        MemberArticle isEditPossibleThisArticleResult = isReadOrEditPossibleThisArticle(request, articleId);
        Article article = isEditPossibleThisArticleResult.getArticle();
        model.addAttribute("article", article);
        return "article/articleEditFormV2";
    }


    @MySecurity
    @PostMapping("/article/{articleId}/edit")
    public String articleEdit(@Valid @ModelAttribute(name = "article") Article editArticle , BindingResult bindingResult,
                              @PathVariable(name = "articleId") Long articleId, HttpServletRequest request) {

        // Validation
        if(editArticle.getStatus() == null){
            bindingResult.reject("StatusError", "Choose Status는 선택할 수 없어요!");
        }

        if (bindingResult.hasErrors()) {
            return "article/articleEditFormV2";
        }


        MemberArticle isEditPossibleThisArticleResult = isReadOrEditPossibleThisArticle(request, articleId);
        Long loginMemberId = getLoginMemberId(request);
        articleService.editNewArticle(articleId, editArticle, loginMemberId);
        return "redirect:/";
    }


    @MySecurity
    @GetMapping("/article/{articleId}/share")
    public String articleShareList(@ModelAttribute(name = "memberSearch") MemberSearch memberSearch,
                                   Model model, @PathVariable(name = "articleId") Long articleId,
                                   HttpServletRequest request) {


        MemberArticle isEditPossibleThisArticleResult = isReadOrEditPossibleThisArticle(request, articleId);
        List<Member> members = memberService.findMemberByMemberSearch(memberSearch);
        model.addAttribute("members", members);
        return "article/articleShareV2";
    }


    @MySecurity
    @GetMapping("/article/share/do")
    public String articleShareDoing(@ModelAttribute ShareForm form, HttpServletRequest request) {


        Long toShareMemberId = form.getMemberId();
        Long shareArticleId = form.getArticleId();

        Long loginMemberId = getLoginMemberId(request);
        articleService.shareArticleWithOthers(toShareMemberId, shareArticleId,loginMemberId);
        return "redirect:/";
    }


    @MySecurity
    @GetMapping("/article/{articleId}/complete")
    public String articleDoComplete(@PathVariable(name = "articleId") Long articleId,
                                    HttpServletRequest request){

        // 보안 로직
        log.info("[게시글 접근 보안 확인]");
        MemberArticle isEditPossibleThisArticleResult = isReadOrEditPossibleThisArticle(request, articleId);

        if (isEditPossibleThisArticleResult == null) {
            log.info("[잘못된 회원의 게시글 접근]");
            return "redirect:/";
        }


        Long loginMemberId = getLoginMemberId(request);
        articleService.completeArticle(articleId, loginMemberId);
        return "redirect:/";
    }




    @MySecurity
    @GetMapping("/article/{articleId}/remove")
    public String articleDoDelete(@PathVariable(name = "articleId") Long articleId,
                                  HttpServletRequest request){

        log.info("REMOVE !!!!!");

        Long loginMemberId = getLoginMemberId(request);


        articleService.deleteArticle(articleId, loginMemberId);
        return "redirect:/";
    }


    //== 로그인 ID 찾기==//

    private Long getLoginMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) session.getAttribute(LOGIN_MEMBER);
        return loginMember.getMemberId();

    }

    // 현재 로그인 멤버 ID + Article Id로 해당 게시글 읽을 수 있는지 확인
    private MemberArticle isReadOrEditPossibleThisArticle(HttpServletRequest request, Long articleId) {
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute(LOGIN_MEMBER);
        return memberArticleService.findMemberArticleByMemberIdAndArticleId(loginMember.getMemberId(), articleId);
    }


}
