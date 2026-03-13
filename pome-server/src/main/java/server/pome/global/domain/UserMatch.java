package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "user_match",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_match_pair",
        columnNames = {"user_low", "user_high"}
    ),
    indexes = {
        @Index(name = "idx_user_match_low", columnList = "user_low"),
        @Index(name = "idx_user_match_high", columnList = "user_high")
    }
)
@Comment("확정된 매칭 사용자 페어 테이블")
public class UserMatch extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("매칭 PK")
  private Long id;

  @Column(name = "user_low", nullable = false)
  @Comment("매칭 사용자 중 작은 ID")
  private Long userLow;

  @Column(name = "user_high", nullable = false)
  @Comment("매칭 사용자 중 큰 ID")
  private Long userHigh;


   // 두 사용자 ID를 정렬하여 하나의 쌍으로 저장
  public static UserMatch of(long a, long b) {
    long low = Math.min(a, b);
    long high = Math.max(a, b);
    return new UserMatch(null, low, high);
  }
}
