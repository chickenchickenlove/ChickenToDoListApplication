package todo.application.set;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

//@Component
@Transactional
@RequiredArgsConstructor
public class BulkUpdateConfig {

    private final MemberRepository memberRepository;


    @PostConstruct
    public void init() {
        memberRepository.bulkUpdateAllMemberGradeNormal();
    }





}