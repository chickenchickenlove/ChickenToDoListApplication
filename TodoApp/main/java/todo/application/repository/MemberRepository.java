package todo.application.repository;

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



    //== 조회 로직==//
    public List<Member> findAllMember() {
        return queryFactory.selectFrom(member).fetch();
    }

    public List<Member> findMemberByJoinId(String JoinId) {
        return queryFactory.selectFrom(member).where(getUserIdQuery(JoinId)).fetch();

    }

    public Member findMemberByJoinIdOneMan(String JoinId) {
        return queryFactory.selectFrom(member).where(getUserIdQuery(JoinId)).fetchOne();

    }


    public Member findMemberById(Long id) {
        return em.find(Member.class, id);
    }

    public List<MemberArticle> findMemberArticleByMemberId(Long memberId) {
        return queryFactory.selectFrom(memberArticle)
                .join(memberArticle.member)
                .where(memberArticle.member.id.eq(memberId)).fetch();
    }



    public List<Member> findMemberByNickname(String nickname) {
        return queryFactory.selectFrom(member)
                .where(member.nickname.eq(nickname)).fetch();
    }


    public List<Member> findMemberByEmail(String email) {
        return queryFactory.selectFrom(member)
                .where(member.email.eq(email)).fetch();
    }





    //== Boolean Expression==//
    private BooleanExpression getUserIdQuery(String joinId) {
        return joinId != null ? member.joinId.eq(joinId) : null;
    }


}
