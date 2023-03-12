package todo.application.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    //== 생성 메서드==//
    public static Member createNewMember(String nickname, String joinId, String password, String email) {

        Member member = new Member();
        member.nickname = nickname;
        member.joinId = joinId;
        member.password = password;
        member.email = email;
        member.memberGrade = MemberGrade.NORMAL;
        return member;
    }

    public static Member createAdminMember(String nickname, String joinId, String password, String email) {

        Member member = new Member();
        member.nickname = nickname;
        member.joinId = joinId;
        member.password = password;
        member.email = email;
        member.memberGrade = MemberGrade.ADMIN;
        return member;
    }

}
