package server.pome.chat.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.ChatField;
import server.pome.global.enums.TypeEnum;

@Repository
public interface ChatFieldRepository extends JpaRepository<ChatField, Long> {

  Optional<ChatField> findByOwner_IdAndPortfolioTypeAndBlockIdAndFieldKey(
      Long ownerId, TypeEnum portfolioType, Long blockId, String fieldKey);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select f from ChatField f where f.id = :id")
  Optional<ChatField> findByIdForUpdate(@Param("id") Long id);

  List<ChatField> findByOwner_IdAndPortfolioType(Long ownerId, TypeEnum portfolioType);
}
