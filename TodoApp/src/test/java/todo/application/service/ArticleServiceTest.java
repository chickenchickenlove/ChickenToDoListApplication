package todo.application.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.Member;

import javax.persistence.EntityManager;
import java.time.LocalDate;

@Slf4j
@Transactional
@SpringBootTest
class ArticleServiceTest {


    @Autowired
    ArticleService articleService;


    @Autowired
    EntityManager em;

    @Test
    @Rollback(value = false)
    void saveArticle() throws Exception{

        String myTitle = "오늘의명언";
        String myContents = "안녕하세요 \n" + "안녕할까요? \n" + "안녕합니다.";
        Article article = Article.createArticle(myTitle, myContents, LocalDate.now());
        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");

        em.persist(newMember);

        em.flush();
        em.clear();


        articleService.saveNewArticle(myTitle, myContents, LocalDate.now(),newMember.getId());
    }


    @Test
    @Rollback(value = false)
    void editArticle() throws Exception{

        String myTitle = "오늘의명언";
        String myContents = "안녕하세요 \n" + "안녕할까요? \n" + "안녕합니다.";
        Article article = Article.createArticle(myTitle, myContents, LocalDate.now());
        Member newMember = Member.createNewMember("abc", "abcd", "abcde", "abcde@naver.com");


        em.persist(newMember);

        em.flush();
        em.clear();

        Long articleNumber = articleService.saveNewArticle(myTitle, myContents, LocalDate.now(),newMember.getId());


        log.info("here??????????");


        String editTitle = "수정용";
        String editContents = "수정을 해볼까요?";

//        articleService.editNewArticle(articleNumber, editTitle, editContents, );
        log.info("here???????????????????");

    }




}