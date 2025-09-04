package server.pome.qualification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Qualification;

import java.util.Optional;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Integer> {
    // 사용자(userId)가 소유한 자격증(qualificationId)만 조회
    Optional<Qualification> findByIdAndPortfolio_User_Id(Long id, Long qualificationId);
}
