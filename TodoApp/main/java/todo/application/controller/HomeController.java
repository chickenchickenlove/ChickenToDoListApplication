package todo.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import todo.application.controller.form.LoginForm;

@Controller
@Slf4j
public class HomeController {

    @RequestMapping("/")
    public String home() {
        log.info("home Controller");
        return "home";
    }

    @GetMapping("/login")
    public String loginHome(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        log.info("Login Home");
        return "login";
    }

    @PostMapping("/login")
    public String loginHome(@ModelAttribute(name = "loginForm") LoginForm form, Model model) {
        log.info("Login form start");
        System.out.println("form = " + form.toString());

        return "redirect:/";
    }




}
