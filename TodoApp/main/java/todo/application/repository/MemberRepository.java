package todo.application.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import todo.application.domain.*;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.util.List;

import static todo.application.domain.QMember.*;
import static todo.application.domain.QMemberArticle.*;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // DI
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    // 저장 로직
    public Long saveMember(Member member) {
        //TODO 회원 가입, 중복 ID 가입 불가능
        em.persist(member);
        return member.getId();
    }



    //== 회원 단건 조회==//
    public Member findMemberByJoinIdOneMan(String JoinId) {
        return queryFactory.selectFrom(member).where(getUserIdQuery(JoinId)).fetchOne();

    }

    public Member findMemberById(Long id) {
        return em.find(Member.class, id);
    }


    //== 회원 리스트 조회==//

    public List<Member> findAllMember() {
        return queryFactory.selectFrom(member).fetch();
    }

    public List<Member> findMemberByJoinId(String JoinId) {
        return queryFactory.selectFrom(member).where(getUserIdQuery(JoinId)).fetch();

    }


    public List<Member> findMemberByNickname(String nickname) {
        return queryFactory.selectFrom(member)
                .where(member.nickname.eq(nickname)).fetch();
    }

    // 유일 조건이기 때문에 Member만 반환한다.
    public Member findMemberByEmail(String email) {
        return queryFactory.selectFrom(member)
                .where(member.email.eq(email)).fetchOne();
    }


    public List<Member> findMemberByMemberSearch(MemberSearch memberSearch) {
        return queryFactory.selectFrom(member)
                .where(getNicknameOrUserId(memberSearch))
                .fetch();
    }


    public List<Member> findMemberByMemberName(String nickname) {
        return queryFactory.selectFrom(member)
                .where((getNicknameLikeQuery(nickname)))
                .fetch();
    }


    //== MemberArticle 조회==//

    public List<MemberArticle> findMemberArticleByMemberId(Long memberId) {
        return queryFactory.selectFrom(memberArticle)
                .join(memberArticle.member)
                .where(memberArticle.member.id.eq(memberId)).fetch();
    }




    //== Boolean Expression==//
    private BooleanExpression getUserIdQuery(String joinId) {
        return joinId != null ? member.joinId.eq(joinId) : null;
    }

    private BooleanExpression getUserIdLikeQuery(String joinId) {
        return joinId != null ? member.joinId.like(joinId) : null;
    }


    private BooleanExpression getNicknameQuery(String nickname) {
        return nickname != null ? member.nickname.eq(nickname) : null;
    }


    private BooleanExpression getNicknameLikeQuery(String nickname) {
        return nickname != null ? member.nickname.like(nickname) : null;
    }

    private BooleanBuilder getNicknameOrUserId(MemberSearch memberSearch) {

        BooleanBuilder builder = new BooleanBuilder();
        if (getNicknameLikeQuery(memberSearch.getNickname()) != null) {
            builder.or(getNicknameLikeQuery(memberSearch.getNickname()));
        }

        if (getUserIdLikeQuery(memberSearch.getJoinId()) != null) {
            builder.or(getUserIdLikeQuery(memberSearch.getJoinId()));
        }

        return builder;
    }


}
