package todo.application;

import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import todo.application.controller.formatter.StringToDateFormatter;
import todo.application.controller.interceptor.LoginInterceptor;

@Component
public class SpringInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/article/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateFormatter());
    }
}
