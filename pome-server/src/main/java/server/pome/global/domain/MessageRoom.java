package server.pome.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
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
@Table(name = "message_rooms")
public class MessageRoom extends BaseEntity {

  @Comment("채팅방 참여자")
  @Column(nullable = false)
  private Long userId;

  @Comment("채팅방 참여자2")
  @Column(nullable = false)
  private Long user2Id;
}
