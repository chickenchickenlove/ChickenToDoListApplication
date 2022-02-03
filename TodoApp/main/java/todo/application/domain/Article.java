package todo.application.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
public class Article {

    //== 테이블 매칭용 ==//
    @Id
    @GeneratedValue
    @Column(name = "article_id")
    private Long id;


    //== 기본 정보==//
    // 작성자
    // 작성일시
    // 작성 제목
    // 작성 내용
    // 공유 대상
    private String writer;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String writeTitle;
    private String writeContents;


    //== 생성 메서드==//

    public static Article createArticle(LocalDateTime createdTime, String writeTitle, String writeContents) {

        Article article = new Article();

        article.createdTime = createdTime;
        article.updatedTime = createdTime;
        article.writeTitle = writeTitle;
        article.writeContents = writeContents;

        return article;
    }


    //== 연관관계 편의 메서드==//




    //== 비즈니스 로직 메서드==//




}
