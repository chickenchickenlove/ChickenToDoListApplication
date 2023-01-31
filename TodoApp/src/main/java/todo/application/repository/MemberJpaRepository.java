package todo.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todo.application.domain.Member;

import java.util.List;

public interface MemberJpaRepository extends JpaRepository<Member, Long>{

    Member findMemberById(Long id);

    Member findMemberByEmail(String email);

    List<Member> findMemberByNickname(String nickname);
}
