package todo.application.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.persistence.FetchType.*;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    public static Article createArticle(String writeTitle, String writeContents, LocalDate dueDate, String writer) {

        Article article = new Article();

        article.writeTitle = writeTitle;
        article.writeContents = writeContents;
        article.dueDate = dueDate;
        article.status = ArticleStatus.ING;
        article.writer = writer;

        return article;
    }

    public void update(LocalDate dueDate, ArticleStatus status, String title, String writeContents) {
        this.dueDate = dueDate;
        this.status = status;
        this.writeTitle = title;
        this.writeContents = writeContents;
    }

    public static Article createArticle(String writeTitle, String writeContents, LocalDate dueDate, Member member) {

        Article article = new Article();

        article.writeTitle = writeTitle;
        article.writeContents = writeContents;
        article.dueDate = dueDate;
        article.status = ArticleStatus.ING;
        article.writer = member.getNickname();

        MemberArticle memberArticle = MemberArticle.createMemberArticle(member, article);

        return article;
    }


    private boolean isThisMemberOwnArticle(Member member) {
        return member.getNickname().equals(this.writer);
    }

    public boolean canEditByThisMember(Member member) {
        return isThisMemberOwnArticle(member);
    }

    public boolean canShareArticle(Long fromMemberId, Long toMemberId) {

        MemberArticle fromMember = this.memberArticles
                .stream()
                .filter(memberArticle -> memberArticle.getMember().getId().equals(fromMemberId))
                .findAny()
                .orElseThrow();

        if (!isThisMemberOwnArticle(fromMember.getMember())) {
            return false;
        }

        return this.memberArticles
                .stream()
                .noneMatch(
                        memberArticle -> memberArticle.getMember().getId().equals(toMemberId)
                );
    }

    public MemberArticle shareToMember(Member toMember) {
        return MemberArticle.shareMemberArticle(toMember, this);
    }
}
