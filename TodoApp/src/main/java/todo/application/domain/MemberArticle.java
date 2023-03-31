package todo.application.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberArticle{

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

    private boolean memberOwn;


    public static MemberArticle createMemberArticle(Member member, Article article) {
        return new MemberArticle(member, article, true);
    }

    public static MemberArticle shareMemberArticle(Member member, Article article) {
        return new MemberArticle(member, article, false);
    }


    private MemberArticle(Member member, Article article, boolean memberOwn) {
        this.member = member;
        this.article = article;
        this.memberOwn = memberOwn;

        member.getArticles().add(this);
        article.getMemberArticles().add(this);
    }

    //== 연관관계 편의 메서드==//
    public void addMemberArticle(Member member, Article article) {
        System.out.println("POINT = " + member.getArticles());

        // 연관관계 셋업
        member.getArticles().add(this);
        article.getMemberArticles().add(this);

        // 현재 엔티티 셋업
        this.member = member;
        this.article = article;

    }


    public static Comparator<MemberArticle> comparator() {
        return Comparator.comparing(o -> o.getArticle().getDueDate());
    }

    public static boolean canDeleteArticleByThisMember(MemberArticle memberArticle) {
        return memberArticle != null;
    }


}
