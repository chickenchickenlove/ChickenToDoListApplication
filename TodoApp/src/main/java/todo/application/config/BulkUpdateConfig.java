package todo.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import todo.application.repository.MemberRepository;

import javax.annotation.PostConstruct;

//@Component
@Transactional
@RequiredArgsConstructor
public class BulkUpdateConfig {

    private final MemberRepository memberRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        memberRepository.bulkUpdateAllMemberGradeNormal();
    }





}