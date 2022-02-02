package todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import todo.application.controller.interceptor.LoginInterceptor;
import todo.application.domain.Member;
import todo.application.service.MemberService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TestDataConfig {


    private final MemberService memberService;

    @PostConstruct
    public void init() {
        memberService.saveMember("test", "test", "test", "abc@abc");
    }




}
