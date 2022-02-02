package todo.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import todo.application.controller.form.LoginForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static todo.application.controller.LoginChar.*;

@Controller
@Slf4j
public class HomeController {

    @RequestMapping("/")
    public String home(HttpServletRequest request) {

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            return "home";
        }

        return "loginOkHome";
    }


    @RequestMapping("/hh")
    public String homeTest(HttpServletRequest request) {

        return "home15";
    }




}
