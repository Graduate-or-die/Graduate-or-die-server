package server.pome.award.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Award;

import java.util.Optional;

@Repository
public interface AwardRepository extends JpaRepository<Award, Integer> {
    // 사용자(userId)가 소유한 수상경력(awardId)만 조회
    Optional<Award> findByIdAndPortfolio_User_Id(Long id, Long userId);
}
