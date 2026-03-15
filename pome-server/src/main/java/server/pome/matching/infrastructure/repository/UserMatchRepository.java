package server.pome.matching.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.pome.global.domain.UserMatch;

public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {

  boolean existsByUserLowAndUserHigh(Long userLow, Long userHigh);

  void deleteByUserLowAndUserHigh(Long userLow, Long userHigh);
}
