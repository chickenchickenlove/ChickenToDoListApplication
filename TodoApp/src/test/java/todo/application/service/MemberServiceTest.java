package todo.application.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todo.application.TestUtils;
import todo.application.controller.form.MemberJoinForm;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    ArticleService articleService;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    MemberJoinForm memberJoinForm;

    @BeforeEach
    void init() {

        memberJoinForm = TestUtils.createNewMemberJoinForm();
//        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
//        em.persist(newMember);
//
//        em.flush();
//        em.clear();
//
//        String myTitle = "오늘의명언";
//        String myContents = "안녕하세요 \n" + "안녕할까요? \n" + "안녕합니다.";
//        articleService.saveNewArticle(myContents, myTitle, LocalDate.now(), newMember.getId());
//
//
//        String myTitle2 = "오늘의명언2";
//        String myContents2 = "안녕하세요2 \n" + "안녕할까요2? \n" + "안녕합니다2.";
//        articleService.saveNewArticle(myContents2, myTitle2, LocalDate.now(), newMember.getId());
    }

    @Test
    void saveMemberSuccessTest() {
        // given
        MemberJoinForm newMemberJoinForm = TestUtils.createNewMemberJoinForm();

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
    void saveMemberFailTest() {
        // given
        MemberJoinForm newMemberJoinForm = TestUtils.createNewMemberJoinForm();
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
    void findByMember() throws Exception {

        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        em.persist(newMember);

        em.flush();

        Long id = memberRepository.findAllMember().get(0).getId();
        List<MemberArticle> articleListByMemberId = memberService.findArticleListByMemberId(id);
        for (MemberArticle memberArticle : articleListByMemberId) {
            System.out.println("Article Write = " + memberArticle.getArticle().getWriter());
            System.out.println("Article title = " + memberArticle.getArticle().getWriteTitle());
            System.out.println("Article Contents = " + memberArticle.getArticle().getWriteContents());
            System.out.println("==============================================");
            System.out.println();
        }
    }

    @Test
    @DisplayName("이메일로 아이디찾기 성공")
    void emailTest1() {

        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        em.persist(newMember);

        em.flush();
        em.clear();

        Member findByMember = memberService.findJoinIdByEmail("abcde@naver.com");
        assertThat(newMember.getId()).isEqualTo(findByMember.getId());
    }


    @Test
    @DisplayName("이메일로 아이디찾기 실패 → Null 발생")
    void emailTest2() {

        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        em.persist(newMember);

        Member findByMember = memberService.findJoinIdByEmail("abcde@naver.comdfdf");

        log.info("new Member = {}", findByMember);
        log.info("findByMember = {}", findByMember);

        assertThat(findByMember).isNull();
    }


    @Test
    @DisplayName("이메일 정상 입력, 아이디 정상 입력, 비번찾기 성공")
    void passwordTest1() {

        //given
        Member member = Member.createNewMember("qwer", "qwer", "qwe!!", "qwer@naver.com");
        em.persist(member);
        em.flush();
        em.clear();


        //when
        Member findPassword = memberService.findPassword(member.getEmail(), member.getPassword());

        //then
        assertThat(findPassword.getPassword()).isEqualTo(member.getPassword());

    }

    @Test
    @DisplayName("이메일  미입력, 아이디 정상 입력, 비번찾기 성공")
    void passwordTest2() throws Exception{
        //given
        Member member = Member.createNewMember("qwer", "qwer", "qwe!!", "qwer@naver.com");
        em.persist(member);
        em.flush();
        em.clear();

        //when
        Member findPassword = memberService.findPassword(null, member.getPassword());

        //then
        assertThat(findPassword).isNull();
    }

    @Test
    @DisplayName("이메일 입력, ID 미입력, 비번찾기 실패")
    void passwordTest3() throws Exception{
        //given
        Member member = Member.createNewMember("qwer", "qwer", "qwe!!", "qwer@naver.com");
        em.persist(member);
        em.flush();
        em.clear();

        //when
        Member findPassword = memberService.findPassword(member.getEmail(), null);

        //then
        assertThat(findPassword).isNull();
    }

    @Test
    @DisplayName("이메일 미입력, ID 미입력, 비번찾기 실패")
    void passwordTest4() throws Exception{
        //given
        Member member = Member.createNewMember("qwer", "qwer", "qwe!!", "qwer@naver.com");
        em.persist(member);
        em.flush();
        em.clear();

        //when
        Member findPassword = memberService.findPassword(null, null);

        //then
        assertThat(findPassword).isNull();
    }


    @Test
    @DisplayName("이메일 오입력, ID 오입력, 비번찾기 실패")
    void passwordTest5() throws Exception{
        //given
        Member member = Member.createNewMember("qwer", "qwer", "qwe!!", "qwer@naver.com");
        em.persist(member);
        em.flush();
        em.clear();

        //when
        Member findPassword = memberService.findPassword("abcde@ab", "abc");

        //then
        assertThat(findPassword).isNull();
    }




}