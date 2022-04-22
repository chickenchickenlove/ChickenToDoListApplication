package todo.application.domain;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
//@NoArgsConstructor
public class RequestShareArticle extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "requestsharedarticle_id")
    private Long id;

    //== 연관관계==//
    @OneToOne
    private Article article;

    @ManyToOne
    private Member toMember;

    //== 필드 가진다==//
    private Long fromMemberId;
    private String fromMemberNickname;
    private String articleTitle;

    //== 생성 메서드==//
    public static RequestShareArticle createRequestShareArticle(Member toMember, Member fromMember, Article shareArticle) {

        RequestShareArticle requestShareArticle = new RequestShareArticle();

        requestShareArticle.toMember = toMember;
        requestShareArticle.article = shareArticle;

        requestShareArticle.fromMemberId = fromMember.getId();
        requestShareArticle.fromMemberNickname = fromMember.getNickname();
        requestShareArticle.articleTitle = shareArticle.getWriteTitle();

        return requestShareArticle;
    }
}