package todo.application.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import todo.application.domain.QVisitorView;
import todo.application.domain.VisitorView;
import todo.application.repository.dto.ReadCountStaticalDto;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public VisitorView findVisitorViewById(Long id) {
        return em.find(VisitorView.class, id);
    }

    public VisitorView findVisitorViewByDate(LocalDate localDate) {
        return queryFactory.selectFrom(visitorView)
                .where(visitorView.date.eq(localDate))
                .fetchOne();
    }

    //== 통계 조회 로직 DTO로 조회==//
    public List<ReadCountStaticalDto> findReadCountStaticalDtoByDate(LocalDate date) {

        List<ReadCountStaticalDto> tempValue = queryFactory.select(Projections.constructor(ReadCountStaticalDto.class,
                        visitorView.date,
                        visitorView.view,
                        visitorView.loginView,
                        visitorView.userJoin,
                        visitorView.writeCreatedNumber))
                .from(visitorView)
                .where(visitorView.date.loe(date))
                .orderBy(visitorView.date.desc())
                .offset(0)
                .limit(7)
                .fetch();

        List<ReadCountStaticalDto> resultList = tempValue.stream().sorted(Comparator.comparing(readCountStaticalDto -> readCountStaticalDto.getDate()))
                .collect(Collectors.toList());

        return resultList;
    }

    //== 삭제 로직==//
    public void removeVisitorView(VisitorView visitorView) {
        em.remove(visitorView);
    }

}
