package todo.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import todo.application.controller.aop.annotation.Retry;
import todo.application.controller.controllerexception.annotation.Monitoring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Enumeration;

import static todo.application.controller.LoginChar.*;

@Monitoring
@Controller
@Slf4j
public class HomeController {

    @Retry
    @RequestMapping("/")
    public String home(HttpServletRequest request) {

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute(LOGIN_MEMBER) == null) {
            return "homeV2";
        }
        return "redirect:/article/list";
    }

}
