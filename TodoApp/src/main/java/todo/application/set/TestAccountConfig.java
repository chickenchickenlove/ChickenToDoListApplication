package todo.application.set;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.domain.VisitorView;
import todo.application.repository.MemberRepository;
import todo.application.repository.VisitorViewRepository;
import todo.application.service.MemberService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;

//@Component
@RequiredArgsConstructor
public class TestAccountConfig {

    private final InitClass initClass;

    @PostConstruct
    public void init() {
        initClass.init();
    }

    @Transactional
    @Component
    static class InitClass{

        private final MemberService memberService;
        private final VisitorViewRepository visitorViewRepository;
        private final MemberRepository memberRepository;


        public InitClass(MemberService memberService, VisitorViewRepository visitorViewRepository, MemberRepository memberRepository) {
            this.memberService = memberService;
            this.visitorViewRepository = visitorViewRepository;
            this.memberRepository = memberRepository;
        }

        public void init() {
            Long memberId = memberService.saveMember("TEST", "test", "1", "test@abc.com");

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





}
