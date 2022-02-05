package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import todo.application.controller.form.LoginForm;
import todo.application.controller.form.MemberArticleForm;
import todo.application.domain.Member;
import todo.application.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    //== 로그인 로직==//


    // 로그인폼 접속
    @GetMapping("/login")
    public String loginHome(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login/loginFormV2";
    }

    // 로그인폼 시작
    @PostMapping("/login")
    public String loginHome(@Validated @ModelAttribute(name = "loginForm") LoginForm form, BindingResult bindingResult, Model model, HttpServletRequest request,
                            @RequestParam(name = "requestURI", defaultValue = "null") String requestUri) {

        // 로그인 실패하면 에러 메세지를 만들어서 보내준다.
        Member findMember = checkLogin(form, bindingResult, request);

        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}",bindingResult);
            return "login/loginFormV2";
        }


        // 로그인 성공 -> 세션 생성
        createNewSession(findMember, request);


        // 인터셉터에 걸려 온 것이 아닐 때,
        // 바로 redirect
        if (requestUri.equals("null")) {
            return "redirect:/";
        }

        log.info("here5");
        // 인터셉터에 걸려 왔을 때
        // 최종 지점으로 redirect
        return "redirect:" + requestUri;
    }


    //== 로그아웃 로직==//

    @GetMapping("/logout")
    public String logOut(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(0);
        session.invalidate();

        return "redirect:/";
    }




    //== 비즈니스 로직==//

    /**
     * 로그인 Validation
     * 1. ID가 존재하는지 확인
     * 2. ID, 비밀번호 매칭되는지 확인
     */
    private Member checkLogin(LoginForm form, BindingResult bindingResult, HttpServletRequest request) {

        // DB에 멤버 있는지 확인
        Member findMember = memberService.findMemberByJoinIdOne(form.getJoinId());

        if (findMember == null) {
            bindingResult.reject("LoginError", "ID가 존재하지 않습니다.");
            return null;
        }

        if (!findMember.getPassword().equals(form.getPassword())) {
            bindingResult.reject("LoginError", "ID, 비밀번호가 틀렸습니다. ");
            return null;
        }

        return findMember;
    }

    /**
     * 로그인 성공 로직
     * - 로그인 성공할 경우, Session에 로그인 정보를 넣어두고 사용함.
     */

    private void createNewSession(Member findMember, HttpServletRequest request) {
        MemberArticleForm findMemberArticleForm = new MemberArticleForm(findMember.getId(), findMember.getNickname(), findMember.getJoinId());
        HttpSession loginSession = request.getSession(true);
        loginSession.setAttribute("loginMember", findMemberArticleForm);
    }


}
