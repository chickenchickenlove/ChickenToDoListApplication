package todo.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.service.MemberService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class TestDataConfig {

    private final MemberService memberService;


    @EventListener(ApplicationReadyEvent.class)
    public void initDatabase() {
        Long memberId = memberService.saveMember("김정수", "1", "1", "abc@abc");

        // Dummy Member
        for (int i = 0; i < 100; i++) {
            memberService.saveMember(UUID.randomUUID().toString().substring(0,4), "test" + i, "test", i + "abc1@abc");
        }

        // Dummy Member
        for (int i = 0; i < 100; i++) {
            memberService.saveMember("가나다" + i, "가나다" + i, "abc", "abc@!ab" + i);
        }

        // Dummy Article
        Member findMember = memberService.findMemberById(memberId);
        for (int i = 0; i < 100; i++) {
            Article saveArticle = Article.createArticle("ARTICLE" +i , "article" + i, LocalDate.now(), findMember.getNickname());
            MemberArticle memberArticle = MemberArticle.createMemberArticle(findMember, saveArticle);
        }


    }
}
