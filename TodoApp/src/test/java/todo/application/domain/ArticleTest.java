package todo.application.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import todo.application.TestUtilsConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static todo.application.TestUtilsConstant.*;


@SpringBootTest
@Slf4j
class ArticleTest {


    @Test
    void createArticle() {

        //when
        Article article = Article.createArticle(ARTICLE_TITLE,ARTICLE_CONTENT,ARTICLE_DUE_DATE,ARTICLE_WRITER);

        //then
        assertThat(article.getWriteTitle()).isEqualTo(ARTICLE_TITLE);
        assertThat(article.getWriteContents()).isEqualTo(ARTICLE_CONTENT);
        assertThat(article.getWriter()).isEqualTo(ARTICLE_WRITER);
        assertThat(article.getDueDate()).isEqualTo(ARTICLE_DUE_DATE);
    }

    @Test
    void updateTest() {
        // given
        Article article = Article.createArticle(ARTICLE_TITLE,ARTICLE_CONTENT,ARTICLE_DUE_DATE,ARTICLE_WRITER);

        // when
        article.update(UPDATE_ARTICLE_DUE_DATE, UPDATE_ARTICLE_STATUS, UPDATE_ARTICLE_TITLE, UPDATE_ARTICLE_CONTENT);

        // then
        assertThat(article.getDueDate()).isEqualTo(UPDATE_ARTICLE_DUE_DATE);
        assertThat(article.getStatus()).isEqualTo(UPDATE_ARTICLE_STATUS);
        assertThat(article.getWriteTitle()).isEqualTo(UPDATE_ARTICLE_TITLE);
        assertThat(article.getWriteContents()).isEqualTo(UPDATE_ARTICLE_CONTENT);
    }
}