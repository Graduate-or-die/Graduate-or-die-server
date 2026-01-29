package server.pome.portfolio.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Portfolio;
import java.util.Collections;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long>, PortfolioPreviewRepositoryCustom{

  Portfolio findByUser_Id(Long userId);

}
interface PortfolioPreviewRepositoryCustom {
  List<Object[]> findPreviewTopN(Long portfolioId, long typeId, int n); // [id, title, awardGrade]

}

// 커스텀 구현 (JPQL에서 visible 조건 제거)
class PortfolioRepositoryImpl implements PortfolioPreviewRepositoryCustom {

  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Object[]> findPreviewTopN(Long portfolioId, long typeId, int n) {
    String jpql = switch ((int) typeId) {
      case 1 -> """
                SELECT e.id, CONCAT(e.school, ' | ', e.major), NULL
                FROM Education e
                WHERE e.portfolio.id = :pid
                ORDER BY e.updatedAt DESC
            """;
      case 2 -> """
                SELECT x.id, x.workplace, NULL
                FROM Experience x
                WHERE x.portfolio.id = :pid
                ORDER BY x.updatedAt DESC
            """;
      case 3 -> """
                SELECT a.id, a.activityName, NULL
                FROM Activity a
                WHERE a.portfolio.id = :pid
                ORDER BY a.updatedAt DESC
            """;
      case 4 -> """
                SELECT w.id, w.awardName, w.awardGrade
                FROM Award w
                WHERE w.portfolio.id = :pid
                ORDER BY w.updatedAt DESC
            """;
      case 5 -> """
                SELECT q.id, q.qualificationName, NULL
                FROM Qualification q
                WHERE q.portfolio.id = :pid
                ORDER BY q.updatedAt DESC
            """;
      case 6 -> """
                SELECT p.id, p.projectName, NULL
                FROM Project p
                WHERE p.portfolio.id = :pid
                ORDER BY p.updatedAt DESC
            """;
      case 7 -> null; // 기타: 항상 빈
      default -> null;
    };
    if (jpql == null) return Collections.emptyList();

    return em.createQuery(jpql, Object[].class)
            .setParameter("pid", portfolioId)
            .setMaxResults(n)
            .getResultList();
  }

  public long countVisibleByType(Long portfolioId, long typeId) {
    String jpql = switch ((int) typeId) {
      case 1 -> "SELECT COUNT(e) FROM Education e WHERE e.portfolio.id = :pid";
      case 2 -> "SELECT COUNT(x) FROM Experience x WHERE x.portfolio.id = :pid";
      case 3 -> "SELECT COUNT(a) FROM Activity a WHERE a.portfolio.id = :pid";
      case 4 -> "SELECT COUNT(w) FROM Award w WHERE w.portfolio.id = :pid";
      case 5 -> "SELECT COUNT(q) FROM Qualification q WHERE q.portfolio.id = :pid";
      case 6 -> "SELECT COUNT(p) FROM Project p WHERE p.portfolio.id = :pid";
      case 7 -> null; // 기타
      default -> null;
    };
    if (jpql == null) return 0L;

    return em.createQuery(jpql, Long.class)
            .setParameter("pid", portfolioId)
            .getSingleResult();
  }
}
