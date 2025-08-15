package server.pome.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Activity;

import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // 사용자(userId)가 소유한 대내외활동(activityId)만 조회
    Optional<Activity> findByIdAndPortfolio_User_Id(Long id, Long userId);
}
