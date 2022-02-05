package todo.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import todo.application.controller.form.LoginForm;
import todo.application.controller.form.MemberArticleForm;
import todo.application.domain.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static todo.application.controller.LoginChar.*;

@Controller
@Slf4j
public class HomeController {

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            return "homeV2";
        }


        return "redirect:/article/list";
    }

}
