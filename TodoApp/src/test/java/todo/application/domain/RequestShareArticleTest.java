package todo.application.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todo.application.TestUtilsConstant;

import javax.persistence.EntityManager;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class RequestShareArticleTest {


    @Test
    void requestShareArticleCreateTest1() {

        //given
        Member fromMember = Member.createNewMember(TestUtilsConstant.MEMBER_NICKNAME, TestUtilsConstant.MEMBER_JOINID,
                TestUtilsConstant.PASSWORD, TestUtilsConstant.EMAIL);
        Member toMember = Member.createNewMember(TestUtilsConstant.TO_MEMBER_NICKNAME, TestUtilsConstant.TO_MEMBER_JOINID,
                TestUtilsConstant.TO_PASSWORD, TestUtilsConstant.TO_EMAIL);
        Article article = Article.createArticle(TestUtilsConstant.ARTICLE_TITLE, TestUtilsConstant.ARTICLE_CONTENT,
                TestUtilsConstant.ARTICLE_DUE_DATE, fromMember.getNickname());

        //when
        RequestShareArticle requestShareArticle = RequestShareArticle.createRequestShareArticle(toMember, fromMember, article);

        //then
        assertThat(requestShareArticle.getFromMemberNickname()).isEqualTo(fromMember.getNickname());
        assertThat(requestShareArticle.getToMember()).isEqualTo(toMember);
        assertThat(requestShareArticle.getArticle()).isEqualTo(article);
    }
}