package server.pome.matching.infrastructure.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.pome.matching.domain.EmbeddingStatus;
import server.pome.matching.domain.UserVectorState;

public interface UserVectorStateRepository extends JpaRepository<UserVectorState, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
        SELECT u
        FROM UserVectorState u
        WHERE u.embeddingStatus = :status
        ORDER BY u.updatedAt ASC
      """)
  List<UserVectorState> findForUpdate(@Param("status") EmbeddingStatus status, Pageable pageable);
}
