package todo.application.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Slf4j
class ArticleTest {


    @Test
    void createArticle() {

        //given
        String myTitle = "오늘의명언";
        String myContents = "안녕하세요 \n" + "안녕할까요? \n" + "안녕합니다.";

        //when
        Article article = Article.createArticle(myTitle, myContents, LocalDate.now());

        //then
        log.info("created Article = {} ", article.toString());
        assertThat(article.getWriteTitle()).isEqualTo("오늘의명언");



    }

}