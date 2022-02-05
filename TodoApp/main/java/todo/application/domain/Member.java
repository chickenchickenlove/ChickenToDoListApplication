package todo.application.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
public class Member extends BaseEntity{

    //== 테이블 매치용 ==//
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //== 연관관계 ==//
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MemberArticle> articles = new ArrayList<>();


    //==  기본 정보 ==//
    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String joinId;
    private String password;
    private String email;

    @Version
    private Long version;

    //== 생성 메서드==//

    public static Member createNewMember(String nickname, String joinId, String password, String email) {

        Member member = new Member();
        member.nickname = nickname;
        member.joinId = joinId;
        member.password = password;
        member.email = email;
        return member;
    }


    //== 연관관계 편의 메서드==//


    //== 비즈니스 로직 메서드==//






}
