package server.pome.global.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_reads")
public class ChatRoomRead extends BaseEntity {

  @Comment("읽은 채팅방")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private ChatRoom room;

  @Comment("읽은 사용자")
  @ManyToOne(fetch = LAZY) @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Comment("마지막으로 읽은 메시지 ID (없으면 0)")
  private Long lastReadMessageId = 0L;

  @Comment("마지막으로 읽은 시각")
  private LocalDateTime lastReadAt = LocalDateTime.MIN;

  public ChatRoomRead(ChatRoom room, User user) {
    this.room = room;
    this.user = user;
  }

  // 읽음 포인터 앞으로 이동
  public void advanceTo(Long messageId, LocalDateTime when) {
    // 메시지가 존재하고, id가 앞서는 경우 포인터 이동
    if (messageId != null && messageId > this.lastReadMessageId) {
      this.lastReadMessageId = messageId;
      this.lastReadAt = when;
    }
  }
}
