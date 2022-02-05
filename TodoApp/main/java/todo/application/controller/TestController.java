package todo.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import todo.application.controller.form.ArticleForm;

@Controller
@Slf4j
public class TestController {


    @GetMapping("/test")
    public String doTest() {
        return "homeV2";
    }

    @GetMapping("/about")
    public String doAbout() {
        return "about";
    }


    @GetMapping("/contact")
    public String doContact() {
        return "contact";
    }

    @GetMapping("/postpost")
    public String doPost() {
        return "post";
    }

    @GetMapping("/write")
    public String doWrite(Model model) {
        ArticleForm articleForm = new ArticleForm();
        model.addAttribute("articleForm", articleForm);
        return "article/articleFormV2";
    }

    @GetMapping("/write1")
    public String gogo(Model model) {
        ArticleForm articleForm = new ArticleForm();
        model.addAttribute("articleForm", articleForm);
        return "article/articleFormV2";
    }


}
