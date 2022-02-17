package todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import todo.application.controller.formatter.LocalDateToStringConverter;
import todo.application.controller.formatter.StringToLocalDateFormatter;
import todo.application.controller.interceptor.AdminLoginInterceptor;
import todo.application.controller.interceptor.LoginInterceptor;
import todo.application.repository.MemberArticleRepository;

@Component
public class SpringInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/article/**", "/share", "/admin/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/bootstrap/**");

        registry.addInterceptor(new AdminLoginInterceptor())
                .order(2)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/bootstrap/**");
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLocalDateFormatter());
        registry.addConverter(new LocalDateToStringConverter());
    }
}
