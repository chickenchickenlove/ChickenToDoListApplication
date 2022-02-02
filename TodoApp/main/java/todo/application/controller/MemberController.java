package todo.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import todo.application.controller.form.MemberJoinForm;
import todo.application.service.MemberService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String saveMemberForm(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "members/memberForm";
    }


    @PostMapping("/members/new")
    public String saveMemberForm(@Validated @ModelAttribute(name = "memberJoinForm") MemberJoinForm form,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // Validation.
        // Validation 실패 시, 회원가입 화면으로 Return.
        checkMemberValidation(form, bindingResult);


        if (bindingResult.hasErrors()) {
            log.info("Error가 있습니다. 회원 가입으로 다시 돌립니다. ");
            return "members/memberForm";
        }

        //성공 로직
        memberService.saveMember(form.getNickname(), form.getJoinId(), form.getPassword(), form.getEmail());

        redirectAttributes.addAttribute("joinId", form.getJoinId());

        return "redirect:/members/completed/{joinId}";
    }



    @GetMapping("/members/completed/{joinId}")
    public String completeJoin(@PathVariable(name = "joinId") String joinId, Model model) {
        model.addAttribute("joinId", joinId);
        return "members/memberSaveComplete";
    }







    private void checkMemberValidation(MemberJoinForm form, BindingResult bindingResult) {

        // 비밀번호 동일한 것인지 확인
        checkPassword(form.getPassword(), form.getPasswordRepeat(), bindingResult);

        // 중복 ID, 별명, Email 있는지 확인.
        canMemberJoin(form, bindingResult);
    }


    private void checkPassword(String password, String passwordRepeat, BindingResult bindingResult) {
        if (!password.equals(passwordRepeat)){
            bindingResult.reject("Password", "동일한 비밀번호를 반복해주세요");
        }
    }


    private void canMemberJoin(MemberJoinForm form, BindingResult bindingResult) {

        if (memberService.isEmailPossible(form.getEmail()) != 0) {
            bindingResult.reject("DuplicateEmail", "동일한 Email이 존재합니다.");
        }

        if (memberService.isJoinIdPossible(form.getJoinId()) != 0) {
            bindingResult.reject("DuplicateJoinId", "동일한 회원 ID가 존재합니다.");
        }

        if (memberService.isNicknamePossible(form.getJoinId()) != 0) {
            bindingResult.reject("DuplicateNickname", "동일한 별명이 존재합니다.");
        }
    }

}
