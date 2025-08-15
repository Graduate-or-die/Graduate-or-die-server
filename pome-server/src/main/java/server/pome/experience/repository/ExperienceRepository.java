package server.pome.experience.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Education;
import server.pome.global.domain.Experience;
import server.pome.global.domain.Portfolio;

import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    // 사용자(userId)가 소유한 경력(experienceId)만 조회
    Optional<Experience> findByIdAndPortfolio_User_Id(Long id, Long userId);
}
