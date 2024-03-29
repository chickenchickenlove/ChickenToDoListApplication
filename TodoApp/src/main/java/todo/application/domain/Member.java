package todo.application.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity{

    //== 테이블 매치용 ==//
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //== 연관관계 ==//
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MemberArticle> articles = new ArrayList<>();

    @OneToMany(mappedBy = "toMember", fetch = FetchType.LAZY)
    private List<RequestShareArticle> shareArticles = new ArrayList<>();

    //==  기본 정보 ==//
    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String joinId;
    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private MemberGrade memberGrade;


    public Member(String nickname, String joinId, String password, String email, MemberGrade memberGrade) {
        this.nickname = nickname;
        this.joinId = joinId;
        this.password = password;
        this.email = email;
        this.memberGrade = memberGrade;
    }

    //== 생성 메서드==//
    public static Member createNewMember(String nickname, String joinId, String password, String email) {

        if (canCreate(nickname, joinId, password, email)) {
            throw new IllegalArgumentException();
        }

        return new Member(nickname, joinId, password, email, MemberGrade.NORMAL);
    }

    public static Member createAdminMember(String nickname, String joinId, String password, String email) {

        if (canCreate(nickname, joinId, password, email)) {
            throw new IllegalArgumentException();
        }

        return new Member(nickname, joinId, password, email, MemberGrade.ADMIN);
    }

    public static boolean canCreate(String nickname, String joinId, String password, String email) {
        return !StringUtils.hasText(nickname) ||
                !StringUtils.hasText(joinId) ||
                !StringUtils.hasText(password) ||
                !StringUtils.hasText(email);
    }

    public static boolean cannotJoinMember(Member member) {
        return Objects.nonNull(member);
    }
    public boolean canJoinMember() {
        return this.id == null;
    }

    public static boolean isValidEmail(String email) {
        return StringUtils.hasText(email);
    }
    public static boolean isValidJoinId(String joinId) {
        return StringUtils.hasText(joinId);
    }


}
