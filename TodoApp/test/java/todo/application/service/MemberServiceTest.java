package todo.application.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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


//    @BeforeEach
    void init() {

        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        em.persist(newMember);

        em.flush();
        em.clear();

        String myTitle = "오늘의명언";
        String myContents = "안녕하세요 \n" + "안녕할까요? \n" + "안녕합니다.";
        articleService.saveNewArticle(myContents, myTitle, LocalDate.now(), newMember.getId());


        String myTitle2 = "오늘의명언2";
        String myContents2 = "안녕하세요2 \n" + "안녕할까요2? \n" + "안녕합니다2.";
        articleService.saveNewArticle(myContents2, myTitle2, LocalDate.now(), newMember.getId());

    }

    @Test
    @Rollback(value = false)
    void findByMember() throws Exception {

        log.info("==============================================================");
        log.info("==============find By Member Test Start=======================");
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
    @Rollback(value = false)
    void 이메일로_아이디찾기_성공() {

        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        em.persist(newMember);

        em.flush();
        em.clear();

        Member findByMember = memberService.findJoinIdByEmail("abcde@naver.com");

        log.info("new Member = {}", System.identityHashCode(newMember));
        log.info("findByMember = {}", System.identityHashCode(findByMember));

        Assertions.assertThat(newMember).isEqualTo(findByMember);
    }


    @Test
    @Rollback(value = false)
    void 이메일로_아이디찾기_실패_NULL발생() {

        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        em.persist(newMember);

        Member findByMember = memberService.findJoinIdByEmail("abcde@naver.comdfdf");
        System.out.println("findByMember = " + findByMember);

        log.info("new Member = {}", findByMember);
        log.info("findByMember = {}", findByMember);


        Assertions.assertThat(findByMember).isNull();
    }



}