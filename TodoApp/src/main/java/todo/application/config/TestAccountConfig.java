package todo.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import todo.application.domain.Member;
import todo.application.domain.VisitorView;
import todo.application.repository.MemberRepository;
import todo.application.repository.VisitorViewRepository;
import todo.application.service.MemberService;

import javax.transaction.Transactional;
import java.time.LocalDate;

//@Component
@RequiredArgsConstructor
@Transactional
public class TestAccountConfig {

    private final MemberService memberService;
    private final VisitorViewRepository visitorViewRepository;
    private final MemberRepository memberRepository;


    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        memberService.saveMember("TEST", "test", "1", "test@abc.com");
        memberRepository.saveMember(Member.createAdminMember("admin", "admin", "1234", "admin@naver.com"));

        int limit = 10;
        Long k = 1L;

        for (int i = 1; i < limit; i++) {
            VisitorView visitorView = new VisitorView();

            LocalDate date = LocalDate.of(2021, 12, i);
            visitorView.setTest(date, k ,k ,k*100 ,k );
            k++;
            visitorViewRepository.saveVisitorView(visitorView);
        }
    }
}
