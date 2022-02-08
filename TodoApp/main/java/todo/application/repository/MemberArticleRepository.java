package todo.application.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import todo.application.domain.MemberArticle;
import todo.application.domain.QArticle;
import todo.application.domain.QMemberArticle;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static todo.application.domain.QArticle.article;
import static todo.application.domain.QMemberArticle.memberArticle;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberArticleRepository {


    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    public MemberArticle findMemberArticleByMemberIdArticleId(Long memberId, Long articleId) {
        return queryFactory.selectFrom(memberArticle)
                .where(memberArticle.member.id.eq(memberId).and(memberArticle.article.id.eq(articleId)))
                .join(memberArticle.article, article).fetchJoin()
                .fetchOne();
    }


    public Slice findSliceArticleByMemberId(Long memberId, Pageable pageable) {

        List<MemberArticle> result = queryFactory.selectFrom(memberArticle)
                .leftJoin(memberArticle.article, article).fetchJoin()
                .where(memberArticle.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .orderBy(memberArticle.article.dueDate.asc())
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



        return new SliceImpl<MemberArticle>(returnList, pageable, hasNextMemberArticleTrue(result, pageable));
    }



    // Slice용 추가

    private boolean hasNextMemberArticleTrue(List<MemberArticle> result, Pageable pageable) {
        return result.size() > pageable.getPageSize() ? true : false;
    }




}
