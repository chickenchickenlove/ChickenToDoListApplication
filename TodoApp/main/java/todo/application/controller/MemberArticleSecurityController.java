package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import todo.application.controller.aop.annotation.MySecurity;
import todo.application.controller.controllerexception.annotation.Monitoring;
import todo.application.controller.form.EditArticleForm;
import todo.application.controller.form.MemberLoginSessionForm;
import todo.application.controller.form.ShareForm;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberSearch;
import todo.application.service.ArticleService;
import todo.application.service.MemberArticleService;
import todo.application.service.MemberService;
import todo.application.service.RequestShareArticleService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

import static todo.application.controller.LoginChar.LOGIN_MEMBER;

@Controller
@Slf4j
@RequiredArgsConstructor
@Monitoring
public class MemberArticleSecurityController {


    private final MemberService memberService;
    private final ArticleService articleService;
    private final MemberArticleService memberArticleService;
    private final RequestShareArticleService requestShareArticleService;


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

        // articleId로 값이 특정됨 → MemberArticle 존재 보장. 따라서 null Check 필요 X
        MemberArticle isReadPossibleThisArticleResult = isReadOrEditPossibleThisArticle(request, articleId);
        Article article = isReadPossibleThisArticleResult.getArticle();
        model.addAttribute("article", article);

        return "article/articleDetailV2";
    }


    @MySecurity
    @GetMapping("/article/{articleId}/edit")
    public String articleEditForm(Model model, @PathVariable(name = "articleId") Long articleId, HttpServletRequest request) {

        // articleId로 값이 특정됨 → MemberArticle 존재 보장. 따라서 null Check 필요 X
        MemberArticle isEditPossibleThisArticleResult = isReadOrEditPossibleThisArticle(request, articleId);
        Article article = isEditPossibleThisArticleResult.getArticle();

        EditArticleForm editArticleForm = new EditArticleForm();
        editArticleForm.setArticleForm(article);


        model.addAttribute("article", editArticleForm);
        return "article/articleEditFormV2";
    }


    @MySecurity
    @PostMapping("/article/{articleId}/edit")
    public String articleEdit(@Valid @ModelAttribute(name = "article") EditArticleForm editArticleForm , BindingResult bindingResult,
                              @PathVariable(name = "articleId") Long articleId, HttpServletRequest request) {

        // Validation
        if(editArticleForm.getStatus() == null){
            bindingResult.reject("StatusError", "Choose Status는 선택할 수 없어요!");
        }

        if (bindingResult.hasErrors()) {
            return "article/articleEditFormV2";
        }


        // articleId로 값이 특정됨 → MemberArticle 존재 보장. 따라서 null Check 필요 X
        MemberArticle isEditPossibleThisArticleResult = isReadOrEditPossibleThisArticle(request, articleId);
        Long loginMemberId = getLoginMemberId(request);

        // select : member / article/ memberArticle --> 쿼리 1회로 줄일 수 있을까?
        // update : article

        // id 바꾸니 service를 바꿔야한다.

        articleService.editNewArticle(articleId, editArticleForm, loginMemberId);

        return "redirect:/";
    }


    @MySecurity
    @GetMapping("/article/{articleId}/share")
    public String articleShareList(@ModelAttribute(name = "memberSearch") MemberSearch memberSearch,
                                   Model model, @PathVariable(name = "articleId") Long articleId,
                                   HttpServletRequest request, Pageable pageable) {

        // Null View에서 Check.
        Slice<Member> members = memberService.findMemberByMemberSearch(memberSearch,pageable);
        model.addAttribute("members", members);


        return "article/articleShareV2";
    }


    @MySecurity
//    @GetMapping("/article/share/do")
    public String articleShareDoing2(@ModelAttribute ShareForm form, HttpServletRequest request) {


        // Form에서 쿼리 파라미터로 MemberId를 넘겨줌 → 존재하는게 보장됨. Null Check 필요 X.
        Long toShareMemberId = form.getMemberId();
        Long shareArticleId = form.getArticleId();

        Long loginMemberId = getLoginMemberId(request);
        articleService.shareArticleWithOthers(toShareMemberId, shareArticleId,loginMemberId);
        return "redirect:/";
    }

    // TODO : BindingResult 점검해야함.
    @MySecurity
    @GetMapping("/article/share/do")
    public String articleShareDoing(@ModelAttribute ShareForm form, HttpServletRequest request, Model model){

        // Form에서 쿼리 파라미터로 MemberId를 넘겨줌 → 존재하는게 보장됨. Null Check 필요 X.
        Long toMemberId = form.getMemberId();
        Long shareArticleId = form.getArticleId();
        Long fromMemberId = getLoginMemberId(request);

        // 이미 공유된 요청은 다시 할 수 없다.
        if(requestShareArticleService.isDuplicated(toMemberId, shareArticleId)){
            model.addAttribute("ERROR", "이미 회원에게 동일한 글 공유가 요청되어있어요 :(");
            return "myerror/WrongState";
        }

        // 공유 리스트에 올려준다.
        requestShareArticleService.saveRequestShareArticle(fromMemberId, toMemberId, shareArticleId);
        return "redirect:/";
    }

    @MySecurity
    @GetMapping("/article/{articleId}/complete")
    public String articleDoComplete(@PathVariable(name = "articleId") Long articleId,
                                    HttpServletRequest request){

        Long loginMemberId = getLoginMemberId(request);
        articleService.completeArticle(articleId, loginMemberId);
        return "redirect:/";

    }


    @MySecurity
    @GetMapping("/article/{articleId}/remove")
    public String articleDoDelete(@PathVariable(name = "articleId") Long articleId,
                                  HttpServletRequest request){

        Long loginMemberId = getLoginMemberId(request);
        articleService.deleteArticle(articleId, loginMemberId);
        return "redirect:/";
    }






    //== 로그인 ID 찾기==//
    private Long getLoginMemberId(HttpServletRequest request) {

        // 로그인 상태 보장됨(Interceptor → null 체크 필요 X)
        HttpSession session = request.getSession(false);
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) session.getAttribute(LOGIN_MEMBER);
        return loginMember.getMemberId();

    }

    // 현재 로그인 멤버 ID + Article Id로 해당 게시글 읽을 수 있는지 확인
    private MemberArticle isReadOrEditPossibleThisArticle(HttpServletRequest request, Long articleId) {
        MemberLoginSessionForm loginMember = (MemberLoginSessionForm) request.getSession().getAttribute(LOGIN_MEMBER);
        return memberArticleService.findMemberArticleByMemberIdAndArticleId(loginMember.getMemberId(), articleId);
    }


}