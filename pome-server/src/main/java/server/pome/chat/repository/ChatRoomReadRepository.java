package server.pome.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.ChatMessage;
import server.pome.global.domain.ChatRoomRead;

@Repository
public interface ChatRoomReadRepository extends JpaRepository<ChatRoomRead, Long> {

  Optional<ChatRoomRead> findByRoomIdAndUserId(Long roomId, Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
    update ChatRoomRead r
       set r.lastReadMessageId = :messageId,
           r.lastReadAt = :now
     where r.room.id = :roomId
       and r.user.id = :userId
       and r.lastReadMessageId < :messageId
  """)
  int advancePointer(@Param("roomId") Long roomId,
      @Param("userId") Long userId,
      @Param("messageId") Long messageId,
      @Param("now") java.time.LocalDateTime now);
}
