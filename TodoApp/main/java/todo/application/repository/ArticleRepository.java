package todo.application.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import todo.application.domain.Article;
import todo.application.domain.MemberArticle;
import todo.application.domain.QArticle;
import todo.application.domain.QMemberArticle;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import java.util.List;

import static todo.application.domain.QArticle.*;
import static todo.application.domain.QMemberArticle.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ArticleRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;


    //== 저장 로직 ==//
    public Long saveArticle(Article article) {
        em.persist(article);
        return article.getId();
    }



    //== 삭제 로직==//
    public void removeMemberArticle(Long articleId) {

        // FK 가진 MemberArticle 먼저 삭제
        queryFactory.delete(memberArticle)
                .where(memberArticle.article.id.eq(articleId))
                .execute();


        // PK Article 삭제
        queryFactory.delete(article)
                .where(article.id.eq(articleId))
                .execute();
    }



    //== 조회 로직 ==//
    public List<Article> AllArticles() {
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


    public List<MemberArticle> findMemberArticleByMemberIdAndArticleId(Long memberId, Long articleId) {
        return queryFactory.selectFrom(memberArticle)
                .where(getMemberArticleQueryByArticleId(articleId), getMemberArticleQueryByMemberId(memberId))
                .fetch();
    }

    public List<MemberArticle> findMemberArticleByArticleId(Long articleId) {
        return queryFactory.selectFrom(memberArticle)
                .where(getMemberArticleQueryByArticleId(articleId))
                .fetch();
    }



        //== 서브 쿼리 로직 ==//
    public BooleanExpression getMemberArticleQueryByMemberId(Long memberId) {
        return memberId != null ? memberArticle.member.id.eq(memberId) : null;
    }

    public BooleanExpression getMemberArticleQueryByArticleId(Long articleId) {
        return articleId != null ? memberArticle.article.id.eq(articleId) : null;
    }
}