package todo.application.repository;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.service.MemberService;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Slf4j
class MemberRepositoryTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;


    @BeforeEach
//    @Rollback(false)
    void init_MemberArticle() {

        Member newMember1 = Member.createNewMember("987765", "987765", "987765", "987765@naver.com");

        for (int i = 0; i < 100; i++) {
            Article article = Article.createArticle("ARTICLE" + i, "abc" + i, LocalDate.now());
            article.setWriter("987765");
            MemberArticle memberArticle1 = new MemberArticle();
            memberArticle1.addMemberArticle(newMember1, article);
        }

        List<MemberArticle> articles = newMember1.getArticles();
        em.persist(newMember1);


        for (int i = 0; i < 100; i++) {
            em.persist(Member.createNewMember("가나다" + i, "가나다" + i, "abc","abc@ab"+i));
        }



    }


    @Test
    void 중복이름가입_실패해야함() {

        Member newMember1 = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        Member newMember2 = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");


        memberRepository.saveMember(newMember1);
        em.flush();
        em.clear();


        memberRepository.saveMember(newMember2);


    }

    @Test
    void save_pass() {
        //given
        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");

        //when
        Long saveMemberId = memberRepository.saveMember(newMember);

        //then
        Member findMember = em.find(Member.class, saveMemberId);
        assertThat(findMember).isEqualTo(newMember);
    }


    @Test
    @DisplayName("가입 ID로 찾는 거 성공")
    void findByJoinId() {

        // when
        Member newMember = Member.createNewMember("qqq123456zbzb", "qqq123456zbzb", "qqq123456zbzb", "qqq123456zbzb@naver.com");
        Long saveMemberId = memberRepository.saveMember(newMember);

        //given
        List<Member> memberByJoinId = memberRepository.findMemberByJoinId("qqq123456zbzb");

        // when
        assertThat(memberByJoinId.get(0).getJoinId()).isEqualTo("qqq123456zbzb");
    }

    @Test
    @DisplayName("가입 ID로 찾는 거 실패")
    void findByJoinIdNo() {

        // when
        Member newMember = Member.createNewMember("qqq123456zbzbsg", "qqq123456zbzbsg", "qqq123456zbzbsg", "qqq123456zbzbsg@naver.com");
        Long saveMemberId = memberRepository.saveMember(newMember);

        //given
        List<Member> memberByJoinId = memberRepository.findMemberByJoinId("qweqweqweqwe");

        // then
        assertThat(memberByJoinId.size()).isEqualTo(0);

    }


    @Test
    @Rollback(value = false)
    void 중복이름_멀티쓰레드_예외발생해야함() throws InterruptedException {

        // 쓰레드 풀 생성성
        ExecutorService service = Executors.newFixedThreadPool(10);

        // 쓰레드 대기
        CountDownLatch latch = new CountDownLatch(10);

        // 쓰레드 시작

            for (int i = 0; i < 10; i++) {
                try {
                    service.execute(() -> {
                        memberRepository.saveMember(Member.createNewMember("abc1234", "abc1234", "abc1234", "abc1234"));
                        latch.countDown();
                    });
                } catch (Exception e) {
                    System.out.println("Exception e " + e);
                }

            }
        latch.await(100, TimeUnit.MILLISECONDS);
    }


    //findMemberByMemberSearch
    @Test
    @DisplayName("memberSearch 모두 null 넣었을 때, 아무것도 안 나와야함")
    void memberSearchNegaTest1() {



        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(new MemberSearch(), pageable);

        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(0);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();
    }


    @Test
    @DisplayName("memberSearch joinId만 null 넣었을 때, 아무것도 안 나와야함")
    void memberSearchNegaTest2() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setNickname("abc");


        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);

        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(0);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();
    }


    @Test
    @DisplayName("memberSearch nickname만 null 넣었을 때, 아무것도 안 나와야함")
    void memberSearchNegaTest3() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("ab125125c");


        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(0);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();
    }



    @Test
    @DisplayName("memberSearch joinId 존재 / nickname 미존재 -> 1개 나와야함 ")
    void memberSearchNegaTest4() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("가나다캬");
        memberSearch.setNickname("가나다키");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(0);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();
    }




    @Test
    @DisplayName("memberSearch nickname 1개만 정상으로 넣으면, 1개만 나와야함")
    void memberSearchPosiTest1() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setNickname("가나다0");


        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);

        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(1);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();

    }


    @Test
    @DisplayName("memberSearch joinId 1개만 정상으로 넣으면, 1개만 나와야함")
    void memberSearchPosiTest2() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("가나다0");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(1);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();
    }


    @Test
    @DisplayName("memberSearch joinId + nickname정상으로 넣으면, 1개만 나와야함")
    void memberSearchPosiTest3() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("가나다0");
        memberSearch.setNickname("가나다0");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(1);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();
    }


    @Test
    @DisplayName("memberSearch joinId / nickname 각각 다른 회원 존재하는거 치면 2개 나와야함")
    void memberSearchPosiTest4() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("가나다1");
        memberSearch.setNickname("가나다0");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(2);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();
    }


    @Test
    @DisplayName("memberSearch joinId 존재 / nickname 미존재 -> 1개 나와야함 ")
    void memberSearchPosiTest5() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("가나다1");
        memberSearch.setNickname("가나다키");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(1);
        assertThat(memberByMemberSearch.hasNext()).isFalse();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isTrue();
    }






}