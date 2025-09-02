package server.pome.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.ChatFieldRead;

@Repository
public interface ChatFieldReadRepository extends JpaRepository<ChatFieldRead, Long> {

  Optional<ChatFieldRead> findByField_IdAndUser_Id(Long fieldId, Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
    update ChatFieldRead r
       set r.lastReadMessageId = :messageId,
           r.lastReadAt        = :now
     where r.field.id          = :fieldId
       and r.user.id           = :userId
       and r.lastReadMessageId < :messageId
  """)
  int advancePointer(@Param("fieldId") Long fieldId,
      @Param("userId") Long userId,
      @Param("messageId") Long messageId,
      @Param("now") java.time.LocalDateTime now);
}

