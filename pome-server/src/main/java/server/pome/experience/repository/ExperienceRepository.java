package server.pome.experience.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Experience;

import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    // 사용자(userId)가 소유한 경력(experienceId)만 조회
    Optional<Experience> findByIdAndPortfolio_User_Id(Long id, Long userId);

    List<Experience> findAllByPortfolio_Id(Long portfolioId);
}
