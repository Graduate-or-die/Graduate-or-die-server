package server.pome.global.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import server.pome.portfolio.type.TypeEnum;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity {

  @Comment("유저-메이트 채팅방")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private ChatRoom room;

  @Comment("채팅 발신자")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Enumerated(EnumType.STRING)
  @Comment("포트폴리오 항목명")
  private TypeEnum portfolioType;

  @Comment("항목 내 블록아이디")
  private Long blockId;

  @Comment("블록 내 필드명")
  private String field;

  @Lob
  @Comment("채팅 내용")
  @Column(nullable = false)
  private String content;



}
