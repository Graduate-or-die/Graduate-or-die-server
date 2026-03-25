package server.pome.global.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity {

  @Comment("유저-메이트 채팅 필드")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "field_id", nullable = false)
  private ChatField field;

  @Comment("채팅 발신자")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Comment("채팅 내용")
  @Lob
  @Column(nullable = false)
  private String content;

}
