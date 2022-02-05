package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import todo.application.controller.form.MemberFindJoinIdForm;
import todo.application.controller.form.MemberJoinForm;
import todo.application.domain.Member;
import todo.application.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/ex")
    public String abc(){
        throw new IllegalStateException();
    }



    @GetMapping("/members/new")
    public String saveMemberForm(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "members/memberFormV2";
    }


    @PostMapping("/members/new")
    public String saveMemberForm(@Validated @ModelAttribute(name = "memberJoinForm") MemberJoinForm form,
                                 BindingResult bindingResult) {

        checkMemberValidation(form, bindingResult);


        if (bindingResult.hasErrors()) {
            log.info("Error가 있습니다. 회원 가입으로 다시 돌립니다. ");
            return "members/memberFormV2";
        }

        //성공 로직

        try {
            memberService.saveMember(form.getNickname(), form.getJoinId(), form.getPassword(), form.getEmail());
            return "redirect:/";
        } catch (Exception e) {
            bindingResult.reject("DuplicatedJoin", "중복회원 가입입니다. ");
            log.info("Error가 있습니다. 회원 가입으로 다시 돌립니다. ");
            return "members/memberFormV2";
        }


    }





    @GetMapping("/members/completed/{joinId}")
    public String completeJoin(@PathVariable(name = "joinId") String joinId, Model model) {
        model.addAttribute("joinId", joinId);
        return "members/memberSaveComplete";
    }


    // E-mail로 ID 찾기

    @GetMapping("/members/findId")
    public String findJoinIdByEmailForm(Model model) {
        model.addAttribute("MemberFindJoinIdForm", new MemberFindJoinIdForm());
        return "members/findJoinIdByEmailForm";
    }

    @PostMapping("/members/findId")
    public String findJoinIdByEmailDo(@Valid @ModelAttribute(name = "memberFindJoinIdForm") MemberFindJoinIdForm form,
                                      BindingResult bindingResult, Model model) {

        //Validation
        if (bindingResult.hasErrors()) {
            return "members/findJoinIdByEmailForm";
        }

        //회원이 없을 때
        Member findMember = memberService.findJoinIdByEmail(form.getEmail());
        if (findMember == null) {
            return "members/findJoinIdByEmailFail";
        }

        //성공 로직
        model.addAttribute("memberId", findMember.getJoinId());
        return "members/findJoinIdByEmailResult";
    }








    private void checkMemberValidation(MemberJoinForm form, BindingResult bindingResult) {
        // 비밀번호 + 반복 비밀번호 Validation
        checkPassword(form.getPassword(), form.getPasswordRepeat(), bindingResult);
    }


    private void checkPassword(String password, String passwordRepeat, BindingResult bindingResult) {
        if (!password.equals(passwordRepeat)) {
            bindingResult.reject("Password", "동일한 비밀번호를 반복해주세요");
        }
    }

}
