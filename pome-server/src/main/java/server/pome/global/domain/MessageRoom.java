package server.pome.global.domain;

import static server.pome.global.exception.BaseResponseStatus.USER_CANNOT_SAME;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // of로만 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "message_rooms")
public class MessageRoom extends BaseEntity {

  @Comment("채팅방 참여자")
  @Column(name = "user_low_id", nullable = false)
  private Long userLowId;

  @Comment("채팅방 참여자2")
  @Column(name = "user_high_id", nullable = false)
  private Long userHighId;

  // 생성 유틸
  public static MessageRoom of(Long u1, Long u2) throws BaseException {
    if (u1 == null || u2 == null) {
      throw new BaseException(USER_NOT_FOUND);
    }

    if (Objects.equals(u1, u2)) {
      throw new BaseException(USER_CANNOT_SAME);
    }

    // 두 ID를 비교하여 작은 건 lowId, 큰 건 highId에 저장
    Long lowId = (Long.compare(u1, u2) <= 0) ? u1 : u2;
    Long highId = (lowId.equals(u1)) ? u2 : u1;

    return new MessageRoom(lowId, highId);
  }

  // 참여자 검증 유틸
  public boolean isParticipant(Long userId) {
    return Objects.equals(userId, userLowId) || Objects.equals(userId, userHighId);
  }

}
