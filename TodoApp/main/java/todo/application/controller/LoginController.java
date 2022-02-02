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


    // 로그인폼 접속
    @GetMapping("/login")
    public String loginHome(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    // 로그인폼 시작
    @PostMapping("/login")
    public String loginHome(@Validated @ModelAttribute(name = "loginForm") LoginForm form, BindingResult bindingResult,Model model, HttpServletRequest request) {
        System.out.println("form = " + form.toString());

        // 로그인 실패하면 에러 메세지를 만들어서 보내준다.

        checkLogin(form, bindingResult, request);
        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}",bindingResult);
            return "login";
        }

        // 로그인 성공하면 세션을 만든다.


        return "redirect:/";
    }


    @GetMapping("/logout")
    public String logOut(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(0);
        session.invalidate();

        return "redirect:/";
    }


    private void checkLogin(LoginForm form, BindingResult bindingResult, HttpServletRequest request) {

        // DB에 멤버 있는지 확인
        Member findMember = memberService.findMemberByJoinIdOne(form.getJoinId());


        if (findMember == null) {
            bindingResult.reject("LoginError", "ID가 존재하지 않습니다.");
            return;
        }

        if (!findMember.getPassword().equals(form.getPassword())) {
            bindingResult.reject("LoginError", "ID, 비밀번호가 틀렸습니다. ");
            return;
        }


        // 세션 생성 + 넣기
        MemberArticleForm findMemberArticleForm = new MemberArticleForm(findMember.getId(), findMember.getNickname(), findMember.getJoinId());
        HttpSession loginSession = request.getSession(true);
        loginSession.setAttribute("loginMember", findMemberArticleForm);


    }

}
