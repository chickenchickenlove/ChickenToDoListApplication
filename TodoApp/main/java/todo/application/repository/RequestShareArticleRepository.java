package todo.application.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.QRequestShareArticle;
import todo.application.domain.RequestShareArticle;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static todo.application.domain.QRequestShareArticle.requestShareArticle;

@Repository
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RequestShareArticleRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public Long saveRequestShareArticle(RequestShareArticle requestShareArticle) {
        em.persist(requestShareArticle);
        return requestShareArticle.getId();
    }

    public RequestShareArticle findRequestShareArticle(Long saveRequestShareArticleId) {
        return em.find(RequestShareArticle.class, saveRequestShareArticleId);
    }

    public void removeRequestShareArticle(RequestShareArticle requestShareArticle) {
        em.remove(requestShareArticle);
    }

    public RequestShareArticle findRequestShareArticleByToMemberIdArticleId(Long toMemberId, Long articleId) {
        return queryFactory.selectFrom(requestShareArticle)
                .where(requestShareArticle.toMember.id.eq(toMemberId), requestShareArticle.article.id.eq(articleId))
                .fetchOne();
    }

    // memberId, requestShareArticleId
    public RequestShareArticle findRequestShareArticleByMemberIdRequestShareArticleId(Long memberId, Long requestShareArticleId) {
        return queryFactory.selectFrom(requestShareArticle)
                .where(requestShareArticle.toMember.id.eq(memberId), requestShareArticle.id.eq(requestShareArticleId))
                .fetchOne();
    }

    public Slice<RequestShareArticle> findSliceRequestShareArticle(Long memberId, Pageable pageable) {
        List<RequestShareArticle> resultList = queryFactory.selectFrom(requestShareArticle)
                .where(requestShareArticle.toMember.id.eq(memberId))
                .orderBy(requestShareArticle.id.asc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize() + 1 )
                .fetch();

        boolean hasNextPage = hasNextSliceRequestShareArticle(resultList, pageable);
        if (resultList.size() == pageable.getPageSize() + 1) {
            resultList.remove(resultList.size() - 1);
        }

        return new SliceImpl<>(resultList, pageable, hasNextPage);
    }

    //== Validation 로직==//
    private boolean hasNextSliceRequestShareArticle(List<RequestShareArticle> resultList, Pageable pageable){
        return resultList.size() > pageable.getPageSize();
    }
}
