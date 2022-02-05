package todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import todo.application.controller.interceptor.LoginInterceptor;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.ArticleRepository;
import todo.application.service.MemberService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataConfig {

    private final MemberService memberService;
    private final InitClass initClass;

    @PostConstruct
    public void init() {
        initClass.init();
    }

    @Transactional
    @Component
    static class InitClass{

        private final MemberService memberService;

        public InitClass(MemberService memberService) {
            this.memberService = memberService;
        }

        public void init() {
            Long memberId = memberService.saveMember("김정수", "1", "1", "abc@abc");

            for (int i = 0; i < 100; i++) {
                memberService.saveMember(UUID.randomUUID().toString().substring(0,4), "test" + i, "test", i + "abc1@abc");
            }

            Article article1 = Article.createArticle("ARTICLE1", "abccbcbaxcbacb", LocalDate.now());
            article1.setWriter("김정수");
            Article article2 = Article.createArticle("ARTICLE2", "abccbcbaxcbacb", LocalDate.now());
            article2.setWriter("김정수");
            Article article3 = Article.createArticle("ARTICLE", "abccbcbaxcbacb", LocalDate.now());
            article3.setWriter("김정수");


            MemberArticle memberArticle1 = new MemberArticle();
            MemberArticle memberArticle2 = new MemberArticle();
            MemberArticle memberArticle3 = new MemberArticle();

            Member findMember = memberService.findMemberById(memberId);
            memberArticle1.addMemberArticle(findMember, article1);
            memberArticle2.addMemberArticle(findMember, article2);
            memberArticle3.addMemberArticle(findMember, article3);






        }
    }





}
