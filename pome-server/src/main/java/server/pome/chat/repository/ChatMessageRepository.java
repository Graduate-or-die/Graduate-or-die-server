package server.pome.chat.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  Optional<ChatMessage> findTopByField_IdOrderByIdDesc(Long fieldId);

  @Query("""
        SELECT DISTINCT m.field.id
        FROM ChatMessage m
        WHERE m.field.id IN :fieldIds
        AND m.deletedAt IS NULL
        AND m.sender.id <> :userId
        AND m.id > coalesce(
          (SELECT max(r.lastReadMessageId)
            FROM ChatFieldRead r
            WHERE r.field = m.field
            AND r.user.id = :userId
          ), 0
        )
      """)
  List<Long> findUnreadFieldIds(@Param("fieldIds") Collection<Long> fieldIds,
      @Param("userId") Long userId);

  Page<ChatMessage> findByFieldIdOrderByIdAsc(Long fieldId, Pageable pageable);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      delete from ChatMessage m
      where m.id = :messageId
        and m.field.id = :fieldId
        and m.sender.id = :userId
      """)
  int deleteOwnedMessage(@Param("messageId") Long messageId,
      @Param("fieldId") Long fieldId,
      @Param("userId") Long userId);
}
