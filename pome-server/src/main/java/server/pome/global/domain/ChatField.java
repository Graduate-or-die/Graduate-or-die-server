package server.pome.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import server.pome.global.enums.TypeEnum;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "chat_fields")
public class ChatField extends BaseEntity {

  @Comment("포트폴리오 소유자")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id", nullable = false)
  private User owner;

  @Comment("포트폴리오 항목명")
  @Enumerated(EnumType.STRING)
  @Column(name = "portfolio_type", nullable = false)
  private TypeEnum portfolioType;

  @Comment("항목 내 블록아이디")
  @Column(name = "block_id")
  private Long blockId;

  @Comment("메시지를 추가한 필드의 식별명")
  @Column(name = "field_key", nullable = false)
  private String fieldKey;

  public boolean isOwner(Long userId) {
    return owner != null && Objects.equals(owner.getId(), userId);
  }

}
