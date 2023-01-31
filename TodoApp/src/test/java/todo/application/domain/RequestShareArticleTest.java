package todo.application.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest
class RequestShareArticleTest {

    @Autowired
    EntityManager em;


    @Test
    @DisplayName("requestShareArticle 정상적으로 생성 확인")
    void requestShareArticleCreateTest1() {

        //given
        Member fromMember = Member.createNewMember("fromMember", "fromMember", "abc", "abc@abc.com");
        Member toMember = Member.createNewMember("toMember", "toMember", "abc", "abdfdfc@abc.com");

        Article article = Article.createArticle("share", "shareshare", LocalDate.now());
        article.setWriter(fromMember.getNickname());

        em.persist(fromMember);
        em.persist(toMember);

        em.flush();
        em.clear();

        //when
        RequestShareArticle requestShareArticle = RequestShareArticle.createRequestShareArticle(toMember, fromMember, article);

        //then
        assertThat(requestShareArticle.getFromMemberId()).isEqualTo(fromMember.getId());
        assertThat(requestShareArticle.getToMember()).isEqualTo(toMember);
        assertThat(requestShareArticle.getArticle()).isEqualTo(article);
        assertThat(requestShareArticle.getArticleTitle()).isEqualTo(article.getWriteTitle());
        assertThat(requestShareArticle.getFromMemberNickname()).isEqualTo(fromMember.getNickname());
    }





}