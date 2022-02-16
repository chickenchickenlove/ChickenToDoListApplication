package todo.application.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import todo.application.domain.QVisitorView;
import todo.application.domain.VisitorView;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static todo.application.domain.QVisitorView.visitorView;

@Repository
@Slf4j
@RequiredArgsConstructor
public class VisitorViewRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;



    //== 저장 로직==//
    public Long saveVisitorView(VisitorView visitorView) {
        em.persist(visitorView);
        return visitorView.getId();
    }



    //== 조회 로직==//
    // PK로 조회
    public VisitorView findVisitorViewById(Long id) {
        return em.find(VisitorView.class, id);
    }


    // 날짜로 조회
    public VisitorView findVisitorViewByDate(LocalDate localDate) {
        return queryFactory.selectFrom(visitorView)
                .where(visitorView.date.eq(localDate))
                .fetchOne();
    }



    //== 삭제 로직==//
    public void removeVisitorView(VisitorView visitorView) {
        em.remove(visitorView);
    }










}
