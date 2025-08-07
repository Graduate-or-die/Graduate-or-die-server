package server.pome.mate.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Mate;
import server.pome.global.domain.User;
import server.pome.global.enums.MateRequestStatus;

@Repository
public interface MateRepository extends JpaRepository<Mate, Long> {

  List<Mate> findByTargetUserAndStatus(User targetUser, MateRequestStatus status);
}
