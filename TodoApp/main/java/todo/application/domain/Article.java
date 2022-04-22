package todo.application.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

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

    @OneToMany(mappedBy = "article", fetch = LAZY)
    private List<MemberArticle> memberArticles = new ArrayList<>();

    //== 기본 정보==//
    private String writer;
    private LocalDate dueDate;
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

}
