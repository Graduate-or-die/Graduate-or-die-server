package server.pome.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import server.pome.global.enums.MateRequestStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "mates")
public class Mate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_user", nullable = false)
  @Comment("메이트 신청 받은 유저")
  private User targetUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_user", nullable = false)
  @Comment("메이트 신청 보낸 유저")
  private User fromUser;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  @Comment("메이트 신청 상태(대기/수락/거절/해제)")
  private MateRequestStatus status;

}
