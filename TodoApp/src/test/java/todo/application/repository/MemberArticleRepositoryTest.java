package todo.application.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
@Slf4j
class MemberArticleRepositoryTest {


    @Autowired
    MemberArticleRepository memberArticleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArticleRepositoryImpl articleRepository;

    @Autowired
    EntityManager em;


    @BeforeEach
    void init_MemberArticle() {

        Member newMember1 = Member.createNewMember("987765", "987765", "987765", "987765@naver.com");
        em.persist(newMember1);
        Article article = null;
        for (int i = 0; i < 100; i++) {
            article = Article.createArticle("ARTICLE" + i, "abc" + i, LocalDate.now());
            article.setWriter("987765");

            if (i % 2 == 0) {
                article.setDueDate(LocalDate.of(2002, 12, 30));
            } else {
                article.setDueDate(LocalDate.of(2002, 12, 1));
            }


            MemberArticle memberArticle1 = new MemberArticle();
            memberArticle1.addMemberArticle(newMember1, article);
        }


        Member newMember2 = Member.createNewMember("qwerq5", "987234234765", "987723423465", "982342347765@naver.com");
        em.persist(newMember2);
        List<Article> allArticles = articleRepository.findAllArticles();
        for (Article article3 : allArticles) {
            MemberArticle memberArticle = new MemberArticle();
            memberArticle.addMemberArticle(newMember2, article3);

        }
    }

    @Test
    @DisplayName("100개 넣고, 10개씩 첫번째 페이지 정상인지 확인 ")
    void sliceTest1() {
        //given
        Member memberByNickname = memberRepository.findMemberByNickname("987765").get(0);

        //when
        PageRequest page = PageRequest.of(0, 10, Sort.by("dueDate"));
        Slice slice = memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberByNickname.getId(), page);

        //then
        assertThat(slice.hasNext()).isTrue();
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasPrevious()).isFalse();
        assertThat(slice.getNumberOfElements()).isEqualTo(10);
        assertThat(slice.getContent().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("100개 넣고, 10개씩 두번째 페이지 정상인지 확인 ")
    void sliceTest2() {
        //given
        Member memberByNickname = memberRepository.findMemberByNickname("987765").get(0);

        //when
        PageRequest page = PageRequest.of(2, 10);
        Slice slice = memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberByNickname.getId(), page);

        //then
        assertThat(slice.hasNext()).isTrue();
        assertThat(slice.isFirst()).isFalse();
        assertThat(slice.hasPrevious()).isTrue();
        assertThat(slice.getNumberOfElements()).isEqualTo(10);
        assertThat(slice.getContent().size()).isEqualTo(10);
    }


    @Test
    @DisplayName("100개 넣고, 10개씩 마지막 페이지 정상인지 확인 ")
    void sliceTest3() {
        //given
        Member memberByNickname = memberRepository.findMemberByNickname("987765").get(0);

        //when
        PageRequest page = PageRequest.of(9, 10);
        Slice slice = memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberByNickname.getId(), page);

        //then
        assertThat(slice.hasNext()).isFalse();
        assertThat(slice.isFirst()).isFalse();
        assertThat(slice.hasPrevious()).isTrue();
        assertThat(slice.getNumberOfElements()).isEqualTo(10);
        assertThat(slice.getContent().size()).isEqualTo(10);
    }


    @Test
    @DisplayName("100개 넣고, 11번째 페이지")
    void sliceTest4() {
        //given
        Member memberByNickname = memberRepository.findMemberByNickname("987765").get(0);

        //when
        PageRequest page = PageRequest.of(20, 10);
        Slice slice = memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberByNickname.getId(), page);

        //then
        assertThat(slice.hasNext()).isFalse();
        assertThat(slice.isFirst()).isFalse();
        assertThat(slice.hasPrevious()).isTrue();
        assertThat(slice.getNumberOfElements()).isEqualTo(0);
        assertThat(slice.getContent().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("100개 넣고, 200개씩 첫번째 페이지")
    void sliceTest5() {
        //given
        Member memberByNickname = memberRepository.findMemberByNickname("987765").get(0);

        //when
        PageRequest page = PageRequest.of(0, 200);
        Slice slice = memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberByNickname.getId(), page);

        //then
        assertThat(slice.hasNext()).isFalse();
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasPrevious()).isFalse();
        assertThat(slice.getNumberOfElements()).isEqualTo(100);
        assertThat(slice.getContent().size()).isEqualTo(100);
    }

    @Test
    @DisplayName("100개 넣고, 200개씩 첫번째 페이지, 기한으로 정렬")
    void sliceTest6() {
        //given
        Member memberByNickname = memberRepository.findMemberByNickname("987765").get(0);

        //when
        PageRequest page = PageRequest.of(0, 200);
        Slice slice = memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberByNickname.getId(), page);

        //then
        for (int i = 0; i < 50; i++) {
            MemberArticle memberArticle = (MemberArticle) slice.getContent().get(i);
            assertThat(memberArticle.getArticle().getDueDate()).isEqualTo(LocalDate.of(2002, 12, 01));
        }
    }

    @Test
    @DisplayName("100개 넣고, 7개씩 첫번째 페이지, 기한으로 정렬")
    void sliceTest7() {
        //given
        Member memberByNickname = memberRepository.findMemberByNickname("987765").get(0);

        //when
        PageRequest page = PageRequest.of(0, 7);
        Slice slice = memberArticleRepository.findSliceArticleByMemberIdNotCompleted(memberByNickname.getId(), page);

        //then
        for (int i = 0; i < 7; i++) {
            MemberArticle memberArticle = (MemberArticle) slice.getContent().get(i);
            assertThat(memberArticle.getArticle().getDueDate()).isEqualTo(LocalDate.of(2002, 12, 01));
        }
    }


    @Test
    @DisplayName("member == article인 memberArticl 찾기 성공")
    void findMemberArticleByMemberIdArticleId2test1() {

        //given

        Member newMember1 = Member.createNewMember("111555", "111555", "111555", "987123123765@naver.com");
        em.persist(newMember1);


        Article article = Article.createArticle("ARTICLE", "abc", LocalDate.now());
        article.setWriter(newMember1.getNickname());
        MemberArticle memberArticle1 = new MemberArticle();
        memberArticle1.addMemberArticle(newMember1, article);

        em.flush();
        em.clear();

        //when
//        MemberArticle findMemberArticle = memberArticleRepository.findMemberArticleByMemberIdArticleId2(newMember1.getId(), article.getId());

        //then
//        assertThat(findMemberArticle.getMember().getNickname()).isEqualTo("111555");


    }


    @Test
    @DisplayName("member == article인 memberArticl 찾을 수 없음(다른 사람 닉네임은 배제됨)")
    void findMemberArticleByMemberIdArticleId2test2() {

        //given

        Member newMember1 = Member.createNewMember("111555", "111555", "111555", "987123123765@naver.com");
        em.persist(newMember1);


        Article article = Article.createArticle("ARTICLE", "abc", LocalDate.now());
        article.setWriter(newMember1.getNickname());
        MemberArticle memberArticle1 = new MemberArticle();
        memberArticle1.addMemberArticle(newMember1, article);

        Member newMember2 = Member.createNewMember("11155555", "11155555", "11155555", "987125353123765@naver.com");
        em.persist(newMember2);


        MemberArticle memberArticle2 = new MemberArticle();
        memberArticle2.addMemberArticle(newMember2, article);


        log.info("memberArticle2 = {}", memberArticle2);
        log.info("article = {}", memberArticle2.getArticle().getWriter());
        log.info("member = {}", memberArticle2.getMember().getNickname());


        em.flush();
        em.clear();

        //when
//        MemberArticle findMemberArticle = memberArticleRepository.findMemberArticleByMemberIdArticleId2(newMember2.getId(), article.getId());

        //then
//        assertThat(findMemberArticle).isNull();



    }








    }




