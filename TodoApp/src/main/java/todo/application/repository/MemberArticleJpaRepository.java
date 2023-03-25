package todo.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todo.application.domain.MemberArticle;

public interface MemberArticleJpaRepository extends JpaRepository<MemberArticle, Long> {
}
