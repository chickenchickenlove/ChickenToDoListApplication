package todo.application.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static todo.application.domain.QArticle.article;
import static todo.application.domain.QMember.*;
import static todo.application.domain.QMemberArticle.memberArticle;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberArticleRepository {

    //DI
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final MemberArticleJpaRepository repository;

    public Long save(MemberArticle memberArticle) {
        return repository.save(memberArticle).getId();
    }

    public void removeByArticleId(Long articleId) {
        repository.removeByArticleId(articleId);
    }

    //== 단건 조회==//
    public MemberArticle findMemberArticleByMemberIdArticleIdAndMemberNickEqualArticleWriter(Long memberId, Long articleId) {
        return queryFactory.selectFrom(memberArticle)
                .join(memberArticle.member, member).fetchJoin()
                .where(memberArticle.member.id.eq(memberId),
                        memberArticle.article.id.eq(articleId),
                        memberArticle.article.writer.eq(memberArticle.member.nickname))
                .fetchOne();
    }

    public MemberArticle findMemberArticleByMemberIdArticleId(Long memberId, Long articleId) {
        return queryFactory.selectFrom(memberArticle)
                .where(memberArticle.member.id.eq(memberId).and(memberArticle.article.id.eq(articleId)))
                .join(memberArticle.article, article).fetchJoin()
                .fetchOne();
    }

    //== 리스트 조회==//
    public List<MemberArticle> findMemberArticleByMemberId(Long memberId) {
        return queryFactory.selectFrom(memberArticle)
                .join(memberArticle.article, article).fetchJoin()
                .where(memberArticle.member.id.eq(memberId))
                .fetch();
    }

    //== 슬라이싱 조회==//
    public Slice<MemberArticle> findSliceArticleByMemberIdNotCompleted(Long memberId, Pageable pageable) {

        // 방어 코드
        if (pageable.getPageSize() == 0) {
            throw new IllegalStateException("잘못된 상태입니다.");
        }

        List<MemberArticle> result = queryFactory.selectFrom(memberArticle)
                .leftJoin(memberArticle.article, article).fetchJoin()
                .where(memberArticle.member.id.eq(memberId), memberArticle.article.status.ne(ArticleStatus.COMPLETE))
                .offset(pageable.getOffset())
                .orderBy(memberArticle.article.dueDate.asc(), memberArticle.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<MemberArticle> returnList = new ArrayList<>();
        int limit = pageable.getPageSize();


        for (MemberArticle memberArticle : result) {
            returnList.add(memberArticle);
            if (--limit == 0) {
                break;
            }
        }

        return new SliceImpl<>(returnList, pageable, hasNextMemberArticleTrue(result, pageable));
    }


    public Slice<MemberArticle> findSliceArticleByMemberIdCompleted(Long memberId, Pageable pageable) {
        // 방어 코드
        if (pageable.getPageSize() == 0) {
            throw new IllegalStateException("잘못된 상태입니다.");
        }

        List<MemberArticle> result = queryFactory.selectFrom(memberArticle)
                .leftJoin(memberArticle.article, article).fetchJoin()
                .where(memberArticle.member.id.eq(memberId), memberArticle.article.status.eq(ArticleStatus.COMPLETE))
                .offset(pageable.getOffset())
                .orderBy(memberArticle.article.dueDate.asc(), memberArticle.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<MemberArticle> returnList = new ArrayList<>();
        int limit = pageable.getPageSize();

        for (MemberArticle memberArticle : result) {
            returnList.add(memberArticle);
            if (--limit == 0) {
                break;
            }
        }

        return new SliceImpl<>(returnList, pageable, hasNextMemberArticleTrue(result, pageable));
    }

    // Slice용 추가
    private boolean hasNextMemberArticleTrue(List<MemberArticle> result, Pageable pageable) {
        return result.size() > pageable.getPageSize() ? true : false;
    }

}
