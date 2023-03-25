package todo.application.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import todo.application.SpringBootBaseTest;
import todo.application.domain.Article;
import todo.application.domain.Member;
import todo.application.domain.MemberArticle;
import todo.application.repository.ArticleRepositoryImpl;
import todo.application.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class ArticleServiceTest extends SpringBootBaseTest {


    @Autowired
    ArticleService articleService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArticleRepositoryImpl articleRepository;

    @Autowired
    EntityManager em;

    @Test
    void saveArticle() {

        // given
        String myTitle = "오늘의명언";
        String myContents = "안녕하세요 \n" + "안녕할까요? \n" + "안녕합니다.";
        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        memberRepository.saveMember(newMember);

        // when
        Long articleId = articleService.saveNewArticle(myTitle, myContents, LocalDate.now(), newMember.getId());

        // then
        Article findArticle = articleRepository.findArticleById(articleId);
        assertThat(findArticle.getId()).isEqualTo(articleId);
    }


    @Test
    void editArticle() throws Exception{


        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");
        String myTitle = "오늘의명언";
        String myContents = "안녕하세요 \n" + "안녕할까요? \n" + "안녕합니다.";
        Article article = Article.createArticle(myTitle, myContents, LocalDate.now(), newMember.getNickname());


        em.persist(newMember);

        em.flush();
        em.clear();

        Long articleNumber = articleService.saveNewArticle(myTitle, myContents, LocalDate.now(),newMember.getId());


        String editTitle = "수정용";
        String editContents = "수정을 해볼까요?";

//        articleService.editNewArticle(articleNumber, editTitle, editContents, );

    }


}