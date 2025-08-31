package server.pome.global.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat_rooms")
public class ChatRoom extends BaseEntity {

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_a_id", nullable = false)
  private User userA;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_b_id", nullable = false)
  private User userB;

  public boolean isParticipant(Long userId) {
    return (this.getUserA().getId().equals(userId) || this.getUserB().getId().equals(userId));
  }
}
