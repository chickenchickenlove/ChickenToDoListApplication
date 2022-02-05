package todo.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Getter
@Setter
@NoArgsConstructor
public class MemberArticle implements Comparable<MemberArticle>{



    // 비교용 Compare
    @Override
    public int compareTo(MemberArticle memberArticle) {

        String myValue = this.getArticle().getDueDate().toString();
        String comparableValue = memberArticle.getArticle().getDueDate().toString();

        int result = myValue.compareTo(comparableValue);
        return result;
    }

    //== 테이블 매칭용 ==//
    @Id @GeneratedValue
    @Column(name = "member_article_id")
    private Long id;


    //== 연관관계 ==//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "article_id")
    private Article article;

    //== 생성 메서드==//

    //== 연관관계 편의 메서드==//

    public void addMemberArticle(Member member, Article article) {
        System.out.println("POINT = " + member.getArticles());
        member.getArticles().add(this);
        this.member = member;
        this.article = article;


        log.info("글 생성 제목 : {}", article.getWriteTitle());
        log.info("글 생성 내용 : {}", article.getWriteContents());

    }

    //== 비즈니스 로직 메서드==//







}
