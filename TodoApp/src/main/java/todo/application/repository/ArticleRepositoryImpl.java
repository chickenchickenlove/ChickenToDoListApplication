package todo.application.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.Article;
import todo.application.domain.MemberArticle;
import todo.application.domain.QArticle;
import todo.application.domain.QMember;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static todo.application.domain.QArticle.*;
import static todo.application.domain.QMember.member;
import static todo.application.domain.QMemberArticle.*;

@Repository
@Slf4j
@Transactional(readOnly = true)
public class ArticleRepositoryImpl {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    private final MemberArticleRepository memberArticleRepository;


    public ArticleRepositoryImpl(JPAQueryFactory queryFactory, EntityManager em, MemberArticleRepository memberArticleRepository) {
        this.queryFactory = queryFactory;
        this.em = em;
        this.memberArticleRepository = memberArticleRepository;
    }

    //== 저장 로직 ==//
    public Long saveArticle(Article article) {
        em.persist(article);
        return article.getId();
    }

    public void removeMemberArticle(Long articleId) {
        memberArticleRepository.removeByArticleId(articleId);
        removeMemberArticleByIdQuery(articleId);
    }

    //== 삭제 로직==//
    public void removeMemberArticleByIdQuery(Long articleId) {
        // PK Article 삭제
        queryFactory.delete(article)
                .where(article.id.eq(articleId))
                .execute();
    }

    //== 조회 로직 ==//
    public List<Article> findAllArticles() {
        return queryFactory.selectFrom(article).fetch();
    }

    public Article findArticleById(Long id) {
        return em.find(Article.class, id);
    }

    public List<MemberArticle> findArticleByMemberId(Long memberId) {
        return queryFactory.selectFrom(memberArticle)
                .leftJoin(memberArticle.article, article).fetchJoin()
                .where(memberArticle.member.id.eq(memberId))
                .fetch();
    }

    public Slice<Article> findSliceArticleByMemberId(Long memberId, Pageable pageable) {

        // 방어 코드
        if (pageable.getPageSize() == 0) {
            throw new IllegalStateException("잘못된 상태입니다.");
        }

        List<MemberArticle> result = queryFactory.selectFrom(memberArticle)
                .leftJoin(memberArticle.article, article).fetchJoin()
                .where(memberArticle.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .orderBy(memberArticle.article.dueDate.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<Article> returnList = new ArrayList<>();
        int limit = pageable.getPageSize();

        for (MemberArticle memberArticle : result) {
            returnList.add(memberArticle.getArticle());
            if (--limit == 0) {
                break;
            }
        }

        return new SliceImpl<>(returnList, pageable, hasNextMemberArticleTrue(result, pageable));
    }

    // 기능 추가 개발 + 테스트 코드 작성 필요
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public MemberArticle findMemberArticleByMemberIdAndArticleId(Long memberId, Long articleId) {
        return queryFactory.selectFrom(memberArticle)
                .where(getMemberArticleQueryByArticleId(articleId), getMemberArticleQueryByMemberId(memberId))
                .fetchOne();
    }

    public List<MemberArticle> findMemberArticleByArticleId(Long articleId) {
        return queryFactory.selectFrom(memberArticle)
                .where(getMemberArticleQueryByArticleId(articleId))
                .fetch();
    }

    //== Where절 쿼리 빌더용 ==//
    public BooleanExpression getMemberArticleQueryByMemberId(Long memberId) {
        return memberId != null ? memberArticle.member.id.eq(memberId) : null;
    }

    public BooleanExpression getMemberArticleQueryByArticleId(Long articleId) {
        return articleId != null ? memberArticle.article.id.eq(articleId) : null;
    }

    //== 페이징 쿼리 용==//
    private boolean hasNextMemberArticleTrue(List<MemberArticle> result, Pageable pageable) {
        return result.size() > pageable.getPageSize();
    }

}