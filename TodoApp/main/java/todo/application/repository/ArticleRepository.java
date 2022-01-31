package todo.application.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import todo.application.domain.Article;
import todo.application.domain.QArticle;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import java.util.List;

import static todo.application.domain.QArticle.*;

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



    //== 조회 로직 ==//
    public List<Article> AllArticles() {
        return queryFactory.selectFrom(article).fetch();
    }


    public Article findArticleById(Long id) {
        return em.find(Article.class, id);
    }








}
