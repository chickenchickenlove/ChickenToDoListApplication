package todo.application.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import todo.application.domain.*;
import todo.application.repository.dto.MemberAdminDto;
import todo.application.repository.dto.MemberDto;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static todo.application.domain.QArticle.article;
import static todo.application.domain.QMember.*;
import static todo.application.domain.QMemberArticle.*;
import static todo.application.domain.QRequestShareArticle.requestShareArticle;


@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberRepository {

    // DI
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    // 저장 로직
    @Transactional
    public Long saveMember(Member member) {
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

    public Member findMemberByEmail(String email) {

        log.info("find member email = {}", email);

        Member member = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.email.eq(email)).fetchOne();

        log.info("find Member = {}", member);

        return member;

    }


    //== 회원 리스트 조회==//

    public List<Member> findAllMember() {
        return queryFactory.selectFrom(member).fetch();
    }

    public Member findMemberByJoinId(String JoinId) {
        return queryFactory.selectFrom(member).where(getUserIdQuery(JoinId)).fetchOne();

    }

    public List<Member> findMemberByNickname(String nickname) {
        return queryFactory.selectFrom(member)
                .where(member.nickname.eq(nickname)).fetch();
    }


    public List<Member> findMemberByMemberName(String nickname) {
        return queryFactory.selectFrom(member)
                .where((getNicknameLikeQuery(nickname)))
                .fetch();
    }


    //== 회원 DTO 조회==//


    public List<MemberAdminDto> findMemberAdminDto() {

        List<MemberDto> tempList = queryFactory.select(Projections.constructor(MemberDto.class,
                        member.id,
                        member.nickname,
                        member.joinId,
                        member.email,
                        member.createdTime))
                .from(member).fetch();

        // LocalDateTime -> String 변환

        return tempList.stream().map(memberDto ->
            new MemberAdminDto(
                    memberDto.getId(),
                    memberDto.getNickname(),
                    memberDto.getJoinId(),
                    memberDto.getEmail(),
                    memberDto.getJoinDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        ).collect(Collectors.toList());

    }


    //== Bulk 연산 ==//

    // 회원 등급 일괄 조정
    @Transactional
    public void bulkUpdateAllMemberGradeNormal() {
        queryFactory.update(member)
                .set(member.memberGrade, MemberGrade.NORMAL)
                .execute();
    }


    // 회원 일괄 삭제
    @Transactional
    public void batchRemoveMember(List<Long> memberIds) {


        // 지울 닉네임을 가져옴
        List<String> writeNickname = queryFactory.select(member.nickname)
                .from(member)
                .where(member.id.in(memberIds).and(member.memberGrade.eq(MemberGrade.NORMAL)))
                .fetch();


        // 연관관계 주인 먼저 삭제
        queryFactory
                .delete(memberArticle)
                .where(memberArticle.member.id.in(memberIds))
                .execute();


        // 나머지 삭제 (지울 닉네임 == 글작성자)
        queryFactory
                .delete(article)
                .where(article.writer.in(writeNickname))
                .execute();

        queryFactory
                .delete(requestShareArticle)
                .where(requestShareArticle.toMember.id.in(memberIds))
                .execute();


        // memberId가 같은 member 삭제
        queryFactory
                .delete(member)
                .where(member.id.in(memberIds).and(member.memberGrade.eq(MemberGrade.NORMAL)))
                .execute();




    }






    //== 슬라이싱 조회==//

    public Slice<Member> findMemberByMemberSearch(MemberSearch memberSearch, Pageable pageable) {

        BooleanBuilder nicknameOrUserId = getNicknameOrUserId(memberSearch);

        if (nicknameOrUserId.getValue() == null) {
            return new SliceImpl<>(new ArrayList<>());
        }

        List<Member> result = queryFactory.selectFrom(member)
                .where(nicknameOrUserId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<Member> returnList = new ArrayList<>();
        int limit = pageable.getPageSize();

        for (Member member : result) {
            returnList.add(member);
            if (--limit == 0) {
                break;
            }

        }

        return new SliceImpl<>(returnList, pageable, hasNextMember(result, pageable));
    }




    //== 비즈니스 로직==//
    private boolean hasNextMember(List<Member> result, Pageable pageable) {
        return result.size() > pageable.getPageSize();
    }


    //== Boolean Expression==//

    private BooleanExpression getUserIdQuery(String joinId) {
        return joinId != null ? member.joinId.eq(joinId) : null;
    }

    private BooleanExpression getUserIdLikeQuery(String joinId) {
        return StringUtils.hasText(joinId) ? member.joinId.like("%" + joinId + "%") : null;
    }


    private BooleanExpression getNicknameQuery(String nickname) {
        return nickname != null ? member.nickname.eq(nickname) : null;
    }


    private BooleanExpression getNicknameLikeQuery(String nickname) {
        return StringUtils.hasText(nickname) ? member.nickname.like("%" + nickname + "%") : null;
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
