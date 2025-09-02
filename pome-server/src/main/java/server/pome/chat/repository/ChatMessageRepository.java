package server.pome.chat.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  // 방의 가장 최신 메시지 1개 조회
  Optional<ChatMessage> findTopByField_IdOrderByIdDesc(Long fieldId);

  // 미읽음 메시지 개수 조회
  // 마지막 읽은 메시지 id보다 큰 메시지 중 내가 보낸 메시지 제외하고 집계
  @Query("""
    SELECT COUNT(m)
      FROM ChatMessage m
     WHERE m.field.id = :fieldId
       AND m.id > COALESCE(:lastReadMessageId, 0)
       AND m.sender.id <> :excludeSenderId
       AND m.deletedAt IS NULL
  """)
  long countUnread(@Param("fieldId") Long fieldId,
      @Param("lastReadMessageId") Long lastReadMessageId,
      @Param("excludeSenderId") Long excludeSenderId);

}
