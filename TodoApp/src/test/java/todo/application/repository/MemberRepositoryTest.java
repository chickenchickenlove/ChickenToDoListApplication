package todo.application.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
            Article article = Article.createArticle("ARTICLE" + i, "abc" + i, LocalDate.now(), newMember1.getNickname());
            MemberArticle.createMemberArticle(newMember1, article);
        }

        List<MemberArticle> articles = newMember1.getArticles();
        em.persist(newMember1);


        for (int i = 0; i < 100; i++) {
            em.persist(Member.createNewMember("가나다" + i, "가나다" + i, "abc","abc@ab"+i));
        }

        em.flush();
        em.clear();



    }


//    @Test
//    void 중복이름가입_실패해야함() {
//
//        Member newMember1 = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
//        Member newMember2 = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
//
//
//        memberRepository.saveMember(newMember1);
//        em.flush();
//        em.clear();
//
//        try {
//            memberRepository.saveMember(newMember2);
//            em.flush();
//            em.clear();
//        } catch (DataIntegrityViolationException a) {
//            log.info("DataIntegrityViolationException = {}", a.getMessage());
//        } catch (DbException b) {
//            log.info("DbException = {}", b.getMessage());
//        } catch (ConstraintViolationException c) {
//            log.info("ConstraintViolationException = {}", c.getMessage());
//        } catch (PersistenceException e) {
//            log.info("PersistenceException = {}", e.getClass());
//        }
//
//        log.info("newMember1 Id = {}", newMember1.getId());
//        log.info("newMember2 Id = {}", newMember2.getId());



//    }

    Member createDummyMember() {
        return Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
    }
    @Test
    void save_pass() {
        //given
        Member newMember = createDummyMember();

        //when
        Long saveMemberId = memberRepository.saveMember(newMember);

        //then
        Member findMember = em.find(Member.class, saveMemberId);
        assertThat(findMember).isEqualTo(newMember);
    }

    @Test
    @DisplayName("Test : findMemberById")
    void test_findMemberById() {
        //given
        Member newMember = createDummyMember();
        Long saveMemberId = memberRepository.saveMember(newMember);

        //when
        Member findMember = memberRepository.findMemberById(saveMemberId);

        //then
        assertThat(findMember.getId()).isEqualTo(newMember.getId());
    }

    @Test
    @DisplayName("Test : findMemberById")
    void test_findMemberByEmail() {
        //given
        Member newMember = createDummyMember();
        Long saveMemberId = memberRepository.saveMember(newMember);

        //when
        Member findMember = memberRepository.findMemberByEmail(newMember.getEmail());

        //then
        assertThat(findMember.getEmail()).isEqualTo(newMember.getEmail());
    }


    @Test
    @DisplayName("가입 ID로 찾는 거 성공")
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
    @DisplayName("가입 ID로 찾는 거 실패")
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
    @DisplayName("memberSearch joinId / nickname 각각 다른 회원 존재하는거 치면 10개 나와야함")
    void memberSearchPosiTest4() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("가나다1");
        memberSearch.setNickname("가나다0");

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
    @DisplayName("memberSearch joinId 존재 / nickname 미존재 -> 10개 나와야함 ")
    void memberSearchPosiTest5() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("가나다1");
        memberSearch.setNickname("가나다키");

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
    @DisplayName("memberSearch joinId + nickname 비슷한 거 뒷쪽에 나올 수 있게 한다면")
    void memberSearchPosiTest6() {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.setJoinId("가나다");
        memberSearch.setNickname("가나다");

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
        memberSearch.setJoinId("가나다");
        memberSearch.setNickname("가나다");

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
    @DisplayName("BULK 삭제 테스트 : 멤버1이 1개의 글을 가지고 있을 때 멤버1 삭제 → 글, 중간 테이블도 다 삭제된다.")
    void bulkDelete1() {


        System.out.println("START TEST ================================");
        //given

        Member newMember = Member.createNewMember("qweqwasdasdaase123e", "qweasdfasfvzxf123qwe", "Qweqwe", "qweasvasd123asdqwe@qweqwe");
        memberRepository.saveMember(newMember);



        Article article = Article.createArticle("ARTICLE", "abc", LocalDate.now(), newMember.getNickname());
        MemberArticle.createMemberArticle(newMember, article);

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
    @DisplayName("BULK 삭제 테스트 : 멤버1이 10개의 글을 가지고 있을 때 멤버1 삭제 → 글, 중간 테이블도 다 삭제된다.")
    void bulkDelete2() {

        int limit = 10;

        System.out.println("START TEST ================================");
        //given

        Member newMember = Member.createNewMember("qweqwasdasdaase123e", "qweasdfasfvzxf123qwe", "Qweqwe", "qweasvasd123asdqwe@qweqwe");
        memberRepository.saveMember(newMember);

        for (int i = 0; i < limit; i++) {
            Article article = Article.createArticle("ARTICLE", "abc", LocalDate.now(), newMember.getNickname());
            MemberArticle memberArticle = MemberArticle.createMemberArticle(newMember, article);
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
    @DisplayName("BULK 삭제 테스트 : 멤버1이 10개의 글을 가지고 있고, 멤버2가 공유받았을 때, 멤버2가 탈퇴하면 글은 하나도 삭제되지 않는다.")
    void bulkDelete3() {

        int limit = 10;

        System.out.println("START TEST ================================");
        //given

        Member newMember1 = Member.createNewMember("qweqwasdasdaase123e", "qweasdfasfvzxf123qwe", "Qweqwe", "qweasvasd123asdqwe@qweqwe");
        Member newMember2 = Member.createNewMember("aazxcxzczxczxczxcasdasd", "aazxcxzczxczxczxcasdasd", "Qweqwe", "aazxcxzczxczxczxcasdasd@qweqwe");
        memberRepository.saveMember(newMember1);
        memberRepository.saveMember(newMember2);

        for (int i = 0; i < limit; i++) {
            Article article = Article.createArticle("ARTICLE", "abc", LocalDate.now(), newMember1.getNickname());
            MemberArticle memberArticle1 = MemberArticle.createMemberArticle(newMember1, article);
            MemberArticle memberArticle2 = MemberArticle.createMemberArticle(newMember2, article);
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