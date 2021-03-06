package todo.application.repository;

import jdk.swing.interop.SwingInterOpUtils;
import lombok.extern.slf4j.Slf4j;
import org.h2.message.DbException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Slf4j
class MemberRepositoryTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberArticleRepository memberArticleRepository;

    @Autowired
    ArticleRepositoryImpl articleRepository;



    @Autowired
    EntityManager em;


    @BeforeEach
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
            em.persist(Member.createNewMember("?????????" + i, "?????????" + i, "abc","abc@ab"+i));
        }

        em.flush();
        em.clear();



    }


    @Test
    void ??????????????????_???????????????() {

        Member newMember1 = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        Member newMember2 = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");


        memberRepository.saveMember(newMember1);
        em.flush();
        em.clear();

        try {
            memberRepository.saveMember(newMember2);
            em.flush();
            em.clear();
        } catch (DataIntegrityViolationException a) {
            log.info("DataIntegrityViolationException = {}", a.getMessage());
        } catch (DbException b) {
            log.info("DbException = {}", b.getMessage());
        } catch (ConstraintViolationException c) {
            log.info("ConstraintViolationException = {}", c.getMessage());
        } catch (PersistenceException e) {
            log.info("PersistenceException = {}", e.getClass());
        }

        log.info("newMember1 Id = {}", newMember1.getId());
        log.info("newMember2 Id = {}", newMember2.getId());



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
    @DisplayName("?????? ID??? ?????? ??? ??????")
    void findByJoinId() {

        // when
        Member newMember = Member.createNewMember("qqq123456zbzb", "qqq123456zbzb", "qqq123456zbzb", "qqq123456zbzb@naver.com");
        Long saveMemberId = memberRepository.saveMember(newMember);

        //given
        Member memberByJoinId = memberRepository.findMemberByJoinId("qqq123456zbzb");

        // when
        assertThat(memberByJoinId.getJoinId()).isEqualTo("qqq123456zbzb");
    }

    @Test
    @DisplayName("?????? ID??? ?????? ??? ??????")
    void findByJoinIdNo() {

        // when
        Member newMember = Member.createNewMember("qqq123456zbzbsg", "qqq123456zbzbsg", "qqq123456zbzbsg", "qqq123456zbzbsg@naver.com");
        Long saveMemberId = memberRepository.saveMember(newMember);

        //given
        Member memberByJoinId = memberRepository.findMemberByJoinId("qweqweqweqwe");

        // then
        assertThat(memberByJoinId).isNull();

    }


    @Test
    void ????????????_???????????????_?????????????????????() throws InterruptedException {

        // ????????? ??? ?????????
        ExecutorService service = Executors.newFixedThreadPool(10);

        // ????????? ??????
        CountDownLatch latch = new CountDownLatch(10);

        // ????????? ??????

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
    @DisplayName("memberSearch ?????? null ????????? ???, ???????????? ??? ????????????")
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
    @DisplayName("memberSearch joinId??? null ????????? ???, ???????????? ??? ????????????")
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
    @DisplayName("memberSearch nickname??? null ????????? ???, ???????????? ??? ????????????")
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
    @DisplayName("memberSearch joinId ?????? / nickname ????????? -> 1??? ???????????? ")
    void memberSearchNegaTest4() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("????????????");
        memberSearch.setNickname("????????????");

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
    @DisplayName("memberSearch nickname 1?????? ???????????? ?????????, 1?????? ????????????")
    void memberSearchPosiTest1() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setNickname("?????????0");


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
    @DisplayName("memberSearch joinId 1?????? ???????????? ?????????, 1?????? ????????????")
    void memberSearchPosiTest2() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("?????????0");

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
    @DisplayName("memberSearch joinId + nickname???????????? ?????????, 1?????? ????????????")
    void memberSearchPosiTest3() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("?????????0");
        memberSearch.setNickname("?????????0");

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
    @DisplayName("memberSearch joinId / nickname ?????? ?????? ?????? ??????????????? ?????? 10??? ????????????")
    void memberSearchPosiTest4() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("?????????1");
        memberSearch.setNickname("?????????0");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(10);
        assertThat(memberByMemberSearch.hasNext()).isTrue();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isFalse();
    }


    @Test
    @DisplayName("memberSearch joinId ?????? / nickname ????????? -> 10??? ???????????? ")
    void memberSearchPosiTest5() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("?????????1");
        memberSearch.setNickname("????????????");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(10);
        assertThat(memberByMemberSearch.hasNext()).isTrue();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isFalse();
    }




    @Test
    @DisplayName("memberSearch joinId + nickname ????????? ??? ????????? ?????? ??? ?????? ?????????")
    void memberSearchPosiTest6() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("?????????");
        memberSearch.setNickname("?????????");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(10);
        assertThat(memberByMemberSearch.hasNext()).isTrue();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isFalse();

        System.out.println("pageable = " + memberByMemberSearch.nextPageable());

    }


    @Test
    @DisplayName("NextPageable")
    void memberSearchPosiTest7() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("?????????");
        memberSearch.setNickname("?????????");

        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Member> memberByMemberSearch = memberRepository.findMemberByMemberSearch(memberSearch, pageable);


        //then
        assertThat(memberByMemberSearch.getContent().size()).isEqualTo(10);
        assertThat(memberByMemberSearch.hasNext()).isTrue();
        assertThat(memberByMemberSearch.hasPrevious()).isFalse();
        assertThat(memberByMemberSearch.isFirst()).isTrue();
        assertThat(memberByMemberSearch.isLast()).isFalse();

        System.out.println("pageable = " + memberByMemberSearch.nextPageable());
        System.out.println("memberByMemberSearch.nextPageable() = " + memberByMemberSearch.nextPageable());

    }



    @Test
    @DisplayName("BULK ?????? ????????? : ??????1??? 1?????? ?????? ????????? ?????? ??? ??????1 ?????? ??? ???, ?????? ???????????? ??? ????????????.")
    void bulkDelete1() {


        System.out.println("START TEST ================================");
        //given

        Member newMember = Member.createNewMember("qweqwasdasdaase123e", "qweasdfasfvzxf123qwe", "Qweqwe", "qweasvasd123asdqwe@qweqwe");
        memberRepository.saveMember(newMember);



        Article article = Article.createArticle("ARTICLE", "abc", LocalDate.now());
        article.setWriter("qweqwasdasdaase123e");
        MemberArticle memberArticle1 = new MemberArticle();
        memberArticle1.addMemberArticle(newMember, article);

        em.flush();
        em.clear();

        ArrayList<Long> list = new ArrayList<>();
        list.add(newMember.getId());



        //when
        memberRepository.batchRemoveMember(list);

        em.flush();
        em.clear();


        //then
        Member findMember = memberRepository.findMemberById(newMember.getId());
        Article findArticle = articleRepository.findArticleById(article.getId());
        MemberArticle findMemberArticle = memberArticleRepository.findMemberArticleByMemberIdArticleId(newMember.getId(), article.getId());


        assertThat(findMember).isNull();
        assertThat(findArticle).isNull();
        assertThat(findMemberArticle).isNull();
    }



    @Test
    @DisplayName("BULK ?????? ????????? : ??????1??? 10?????? ?????? ????????? ?????? ??? ??????1 ?????? ??? ???, ?????? ???????????? ??? ????????????.")
    void bulkDelete2() {

        int limit = 10;

        System.out.println("START TEST ================================");
        //given

        Member newMember = Member.createNewMember("qweqwasdasdaase123e", "qweasdfasfvzxf123qwe", "Qweqwe", "qweasvasd123asdqwe@qweqwe");
        memberRepository.saveMember(newMember);

        for (int i = 0; i < limit; i++) {
            Article article = Article.createArticle("ARTICLE", "abc", LocalDate.now());
            article.setWriter("qweqwasdasdaase123e");
            MemberArticle memberArticle1 = new MemberArticle();
            memberArticle1.addMemberArticle(newMember, article);
        }


        em.flush();
        em.clear();

        ArrayList<Long> list = new ArrayList<>();
        list.add(newMember.getId());


        //when
        memberRepository.batchRemoveMember(list);

        em.flush();
        em.clear();


        //then
        Member findMember = memberRepository.findMemberById(newMember.getId());
        List<MemberArticle> findMemberArticles = memberArticleRepository.findMemberArticleByMemberId(newMember.getId());

        assertThat(findMember).isNull();
        assertThat(findMemberArticles.size()).isEqualTo(0);
    }



    @Test
    @DisplayName("BULK ?????? ????????? : ??????1??? 10?????? ?????? ????????? ??????, ??????2??? ??????????????? ???, ??????2??? ???????????? ?????? ????????? ???????????? ?????????.")
    void bulkDelete3() {

        int limit = 10;

        System.out.println("START TEST ================================");
        //given

        Member newMember1 = Member.createNewMember("qweqwasdasdaase123e", "qweasdfasfvzxf123qwe", "Qweqwe", "qweasvasd123asdqwe@qweqwe");
        Member newMember2 = Member.createNewMember("aazxcxzczxczxczxcasdasd", "aazxcxzczxczxczxcasdasd", "Qweqwe", "aazxcxzczxczxczxcasdasd@qweqwe");
        memberRepository.saveMember(newMember1);
        memberRepository.saveMember(newMember2);

        for (int i = 0; i < limit; i++) {
            Article article = Article.createArticle("ARTICLE", "abc", LocalDate.now());
            article.setWriter("qweqwasdasdaase123e");
            MemberArticle memberArticle1 = new MemberArticle();
            MemberArticle memberArticle2 = new MemberArticle();
            memberArticle1.addMemberArticle(newMember1, article);
            memberArticle2.addMemberArticle(newMember2, article);

        }

        em.flush();
        em.clear();



        ArrayList<Long> list = new ArrayList<>();
        list.add(newMember2.getId());


        //when
        memberRepository.batchRemoveMember(list);

        em.flush();
        em.clear();


        //then
        Member findMember = memberRepository.findMemberById(newMember2.getId());
        List<MemberArticle> removeMemberArticle = memberArticleRepository.findMemberArticleByMemberId(newMember2.getId());
        List<MemberArticle> remainMemberArticle = memberArticleRepository.findMemberArticleByMemberId(newMember1.getId());

        List<Long> articleIdList = remainMemberArticle.stream().map(MemberArticle::getId).collect(Collectors.toList());


        assertThat(findMember).isNull();
        assertThat(removeMemberArticle.size()).isEqualTo(0);
        assertThat(remainMemberArticle.size()).isEqualTo(limit);
        assertThat(articleIdList.size()).isEqualTo(limit);

    }



}