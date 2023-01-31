package todo.application.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todo.application.domain.Member;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ExceptionRepository {

    private final EntityManager em;

    public Member findMember(){
        // 조회결과 없음
        return em.createQuery("SELECT m FROM Member m WHERE m.id = :id", Member.class)
                .setParameter("id", 999L)
                .getSingleResult();
    }

    public void saveMember(){
        // 조회결과 없음
        em.persist(Member.createNewMember("abc1111","abc1111","abc1111","abc1111"));
    }


}
