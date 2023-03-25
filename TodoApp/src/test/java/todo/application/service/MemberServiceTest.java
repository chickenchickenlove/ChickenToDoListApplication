package todo.application.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todo.application.SpringBootBaseTest;
import todo.application.TestUtils;
import todo.application.controller.form.MemberJoinForm;
import todo.application.domain.Member;
import todo.application.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class MemberServiceTest extends SpringBootBaseTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    MemberJoinForm newMemberJoinForm;

    @BeforeEach
    void init() {
        newMemberJoinForm = TestUtils.createNewMemberJoinForm();
    }

    @Test
    void saveMemberSuccessTest() {
        // given


        // when
        Long memberId = memberService.saveMember(newMemberJoinForm.getNickname(),
                newMemberJoinForm.getJoinId(),
                newMemberJoinForm.getPassword(),
                newMemberJoinForm.getEmail());

        // then
        Member findMember = memberRepository.findMemberById(memberId);
        assertThat(findMember.getId()).isEqualTo(memberId);
        assertThat(findMember.getPassword()).isEqualTo(findMember.getPassword());
        assertThat(findMember.getEmail()).isEqualTo(findMember.getEmail());
        assertThat(findMember.getJoinId()).isEqualTo(findMember.getJoinId());
        assertThat(findMember.getNickname()).isEqualTo(findMember.getNickname());
    }

    // Duplicated Entity
    @Test
    void saveMemberFailTestBecauseOfDuplicateJoinID() {
        // given
        memberService.saveMember(newMemberJoinForm.getNickname(),
                newMemberJoinForm.getJoinId(),
                newMemberJoinForm.getPassword(),
                newMemberJoinForm.getEmail());

        // when + then
        assertThatThrownBy(() -> memberService.saveMember(newMemberJoinForm.getNickname(),
                newMemberJoinForm.getJoinId(),
                newMemberJoinForm.getPassword(),
                newMemberJoinForm.getEmail()))
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    void findAllMemberTest() throws Exception {
        // given
        Member member1 = TestUtils.createMemberForTest("test1-");
        Member member2 = TestUtils.createMemberForTest("test2-");
        em.persist(member1);
        em.persist(member2);
        flushAndClear();

        // when
        List<Member> allMember = memberRepository.findAllMember();

        // then
        assertThat(allMember.size()).isEqualTo(2);
    }

    @Test
    void findMemberByEmailSuccessTest() {
        // given
        Member member = TestUtils.createMemberForTest(null);
        em.persist(member);
        flushAndClear();

        // when
        Member findMember = memberService.findMemberByEmail(member.getEmail());

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getEmail()).isEqualTo(findMember.getEmail());
    }


    @Test
    void findMemberByEmailFailTest() {
        // given
        Member member = TestUtils.createMemberForTest(null);
        em.persist(member);
        flushAndClear();

        // when + then
        assertThatThrownBy(() -> memberService.findMemberByEmail(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> memberService.findMemberByEmail(null))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void findPasswordSuccessTest() {
        //given
        Member member = TestUtils.createMemberForTest(null);
        em.persist(member);
        flushAndClear();

        //when
        String password = memberService.findPassword(member.getEmail(), member.getPassword());

        //then
        assertThat(password).isEqualTo(member.getPassword());
    }

    @Test
    void findPasswordFailTest() {
        //given
        Member member = TestUtils.createMemberForTest(null);
        em.persist(member);
        flushAndClear();

        //when
        String password = memberService.findPassword(null, member.getPassword());

        //then
        assertThat(password).isNull();
    }


    private void flushAndClear() {
        em.flush();
        em.clear();
    }

}