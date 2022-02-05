package todo.application.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
public class Article extends BaseEntity {

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
    private LocalDate dueDate;
//    private LocalDateTime createdTime;
//    private LocalDateTime updatedTime;
    private String writeTitle;
    private String writeContents;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Version
    private Long version;


    //== 생성 메서드==//

    public static Article createArticle(String writeTitle, String writeContents, LocalDate dueDate) {

        Article article = new Article();

        article.writeTitle = writeTitle;
        article.writeContents = writeContents;
        article.dueDate = dueDate;
        article.status = ArticleStatus.ING;

        return article;
    }


    //== 연관관계 편의 메서드==//




    //== 비즈니스 로직 메서드==//




}
