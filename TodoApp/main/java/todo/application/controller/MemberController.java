package todo.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import todo.application.controller.form.MemberJoinForm;

@Controller
@Slf4j
public class MemberController {


    @GetMapping("/members/new")
    public String saveMemberForm(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        log.info("memberSaveForm Come");
        return "members/memberForm";
    }


    @PostMapping("/members/new")
    public String saveMemberForm(@ModelAttribute(name = "memberJoinForm") MemberJoinForm form, Model model) {
        log.info("memberJoinForm = {}", form.toString());
        return "redirect:/";
    }



}
