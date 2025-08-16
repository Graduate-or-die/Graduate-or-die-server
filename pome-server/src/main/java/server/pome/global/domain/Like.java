package server.pome.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "likes",
    uniqueConstraints = { // {from_user, target_user} 쌍을 유일하게 강제하여 중복 좋아요 방지
        @UniqueConstraint(name = "ux_likes_from_target", columnNames = {"from_user", "target_user"})
    },
    indexes = { // 비유니크 인덱스 생성 -> 조회 및 검색 최적화
        @Index(name = "ix_likes_target", columnList = "target_user"),
        @Index(name = "ix_likes_from", columnList = "from_user")
    }
)
public class Like {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_user", nullable = false,
      foreignKey = @ForeignKey(name = "fk_likes_target_user"))
  @Comment("좋아요를 받은 유저")
  private User targetUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_user", nullable = false,
      foreignKey = @ForeignKey(name = "fk_likes_from_user"))
  @Comment("좋아요를 누른 유저")
  private User fromUser;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

}
