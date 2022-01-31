package todo.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MemberArticle {

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
        member.getArticles().add(this);
        this.member = member;
        this.article = article;

        article.setWriter(member.getJoinId());




    }

    //== 비즈니스 로직 메서드==//







}
