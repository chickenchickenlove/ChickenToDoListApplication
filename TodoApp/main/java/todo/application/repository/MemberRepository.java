package todo.application.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.*;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static todo.application.domain.QMember.*;
import static todo.application.domain.QMemberArticle.*;


@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // DI
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    // 저장 로직
    @Transactional(readOnly = false)
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

    public Member findMemberByEmailAndJoinId(String email, String joinID) {
        return queryFactory.selectFrom(member)
                .where(member.email.eq(email).and(member.joinId.eq(joinID)))
                .fetchOne();
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
        return queryFactory.selectFrom(QMember.member)
                .where(QMember.member.email.eq(email)).fetchOne();

    }


    public Slice<Member> findMemberByMemberSearch(MemberSearch memberSearch, Pageable pageable) {

        BooleanBuilder nicknameOrUserId = getNicknameOrUserId(memberSearch);

        if (nicknameOrUserId.getValue() == null) {
            return new SliceImpl<Member>(new ArrayList<Member>());
        }

        List<Member> result = queryFactory.selectFrom(member)
                .where(nicknameOrUserId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        List<Member> returnList = new ArrayList<>();
        int limit = pageable.getPageSize();


        for (Member member : result) {
            returnList.add(member);
            if (--limit == 0) {
                break;
            }

        }


        return new SliceImpl<Member>(returnList, pageable, hasNextMember(result, pageable));


    }


    private boolean hasNextMember(List<Member> result, Pageable pageable) {
        return result.size() > pageable.getPageSize() ? true : false;
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
