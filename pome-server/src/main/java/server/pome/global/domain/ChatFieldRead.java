package server.pome.global.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "chat_field_reads")
public class ChatFieldRead extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "field_id", nullable = false)
  private ChatField field;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Comment("마지막으로 읽은 메시지 ID")
  private Long lastReadMessageId = 0L;

  @Comment("마지막으로 메시지를 읽은 시각")
  // 기본값은 최소로 설정
  private LocalDateTime lastReadAt = LocalDateTime.of(1970, 1, 1, 0, 0);

  public ChatFieldRead(ChatField field, User user) {
    this.field = field;
    this.user = user;
  }

  // 읽음 포인터 앞으로 이동
  public void advanceTo(Long messageId, LocalDateTime when) {
    // 읽은 메시지가 포인터보다 앞서면 포인터 이동
    if (messageId != null && messageId > this.lastReadMessageId) {
      this.lastReadMessageId = messageId;
      this.lastReadAt = when;
    }
  }
}
