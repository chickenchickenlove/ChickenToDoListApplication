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
import todo.application.controller.aop.annotation.Retry;
import todo.application.controller.form.*;
import todo.application.domain.Member;
import todo.application.service.MemberService;
import todo.application.service.VisitorViewService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;
    private final VisitorViewService visitorViewService;

    // 로그인폼 접속
    @Retry
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
        // Null Check 필요 X : BindingResult에 있음.
        Member findMember = checkLogin(form, bindingResult, request);

        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}",bindingResult);
            return "login/loginFormV2";
        }

        // 로그인 성공 -> 세션 생성
        createNewSession(findMember, request);
        visitorViewService.addLoginView();

        return requestUri.equals("null") ? "redirect:/" : "redirect:" + requestUri;
    }

    //== 로그아웃 로직==//
    @GetMapping("/logout")
    @Retry
    public String logOut(HttpServletRequest request) {
        HttpSession session = request.getSession();

        // 세션 만료 + 제거
        session.setMaxInactiveInterval(0);
        session.invalidate();

        return "redirect:";
    }

    //== ID 찾기 ==//
    @Retry
    @GetMapping("/forgetid")
    public String forgetIdForm(Model model) {
        model.addAttribute("loginForgetIdForm", new LoginForgetIdForm());
        return "login/loginForgetId";
    }

    @PostMapping("/forgetid")
    public String forgetIdView(@Valid @ModelAttribute(name = "loginForgetIdForm") LoginForgetIdForm form,
                               BindingResult bindingResult, Model model) {

        // input Validation
        if (bindingResult.hasErrors()) {
            log.info("Binding Result = {}", bindingResult);
            return "login/loginForgetId";
        }
        // DB Validation
        Member findMember = memberService.findMemberByEmail(form.getEmail());

        // null Check
        if (findMember == null) {
            bindingResult.reject("NoSuchEmailId", "가입된 회원이 없습니다.");
        }

        if (bindingResult.hasErrors()) {
            log.info("Binding Result = {}", bindingResult);
            return "login/loginForgetId";
        }

        form.setJoinId(findMember.getJoinId());
        return "login/loginForgetIdOk";
    }

    //== 비밀번호 찾기 ==//
    @GetMapping("/forgetpassword")
    @Retry
    public String forgetPasswordForm(Model model) {
        model.addAttribute("loginForgetPasswordForm", new LoginForgetPasswordForm());
        return "login/loginForgetPassword";
    }

    @PostMapping("/forgetpassword")
    public String forgetPasswordView(
            @Valid @ModelAttribute(name = "loginForgetPasswordForm") LoginForgetPasswordForm form,
            BindingResult bindingResult) {

        // input Validation
        if (bindingResult.hasErrors()) {
            log.info("Binding Result = {}", bindingResult);
            return "login/loginForgetPassword";
        }

        String password = memberService.findPassword(form.getEmail(), form.getJoinId());

        if (Objects.isNull(password)) {
            bindingResult.reject("NoSuchEmailId", "Email과 일치하는 ID가 없습니다.");
            log.info("Binding Result = {}", bindingResult);
            return "login/loginForgetPassword";
        }

        form.setPassword(password);
        return "login/loginForgetPasswordOk";
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
        MemberLoginSessionForm loginMemberForm = new MemberLoginSessionForm(findMember.getId(), findMember.getNickname(), findMember.getJoinId(), findMember.getMemberGrade());
        HttpSession loginSession = request.getSession(true);
        loginSession.setAttribute("loginMember", loginMemberForm);
    }


}
