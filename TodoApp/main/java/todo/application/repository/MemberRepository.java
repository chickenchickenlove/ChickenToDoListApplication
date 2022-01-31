package todo.application.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
        em.persist(member);
        return member.getId();
    }

    //== 조회 로직==//
    public List<Member> findAllMember() {
        return queryFactory.selectFrom(member).fetch();
    }

    public Member findMemberByJoinId(String JoinId) {
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



    //== Boolean Expression==//
    private BooleanExpression getUserIdQuery(String joinId) {
        return joinId != null ? member.joinId.eq(joinId) : null;
    }


}
