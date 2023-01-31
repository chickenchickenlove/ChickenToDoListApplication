package todo.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Member;
import todo.application.repository.MemberRepository;

import javax.annotation.PostConstruct;

//@Component
@RequiredArgsConstructor
@Transactional
public class AdminConfig {

    private final MemberRepository memberRepository;


    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        Member adminMember = Member.createAdminMember("admin1234", "admin1234","1","admin1@gmail.com");
        Member adminMember1 = Member.createAdminMember("admin12345", "admin12314","1","admin2qweqwe31@gmail.com");
        Member adminMember2 = Member.createAdminMember("admin123456", "admin1231456","1","adminwe45231@gmail.com");
        Member adminMember3 = Member.createAdminMember("admin123457", "admin1231674","1","admin2qwe45331@gmail.com");
        memberRepository.saveMember(adminMember);
        memberRepository.saveMember(adminMember1);
        memberRepository.saveMember(adminMember2);
        memberRepository.saveMember(adminMember3);

    }


}
