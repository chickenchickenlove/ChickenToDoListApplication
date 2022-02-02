package todo.application.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Member;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Slf4j
class MemberRepositoryTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    void save_pass() {
        //given
        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com", LocalDateTime.now());

        //when
        Long saveMemberId = memberRepository.saveMember(newMember);

        //then
        Member findMember = em.find(Member.class, saveMemberId);
        Assertions.assertThat(findMember).isEqualTo(newMember);
        log.info("new Member id = {} , find Member id = {}", newMember.getId(), findMember.getId());
    }


    @Test
    @DisplayName("가입 ID로 찾는 거 성공")
    void findByJoinId() {

        System.out.println("MemberRepositoryTest.findByJoinId");
        // when
        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com", LocalDateTime.now());
        Long saveMemberId = memberRepository.saveMember(newMember);

        em.flush();
        em.clear();

        //given
        List<Member> memberByJoinId = memberRepository.findMemberByJoinId("abcd");

        // when
        Assertions.assertThat(memberByJoinId.get(0).getJoinId()).isEqualTo("abcd");
    }

    @Test
    @DisplayName("가입 ID로 찾는 거 실패")
    void findByJoinIdNo() {

        System.out.println("MemberRepositoryTest.findByJoinId");
        // when
        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com", LocalDateTime.now());
        Long saveMemberId = memberRepository.saveMember(newMember);

        em.flush();
        em.clear();

        //given
        List<Member> memberByJoinId = memberRepository.findMemberByJoinId("qweqweqweqwe");

        // when
        assertThrows(NullPointerException.class, () -> {
            memberByJoinId.get(0).getJoinId();
        });
    }



}