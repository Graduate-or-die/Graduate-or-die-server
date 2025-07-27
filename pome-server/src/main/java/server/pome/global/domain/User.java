package server.pome.global.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, unique = true)
  private Long id;

  @Column(name = "password", nullable = false)
  @Comment("비밀번호")
  private String password;

  @ElementCollection
  @CollectionTable(name = "user_from_ids", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "from_id")
  @Comment("신청자 아이디 목록")
  private List<Integer> fromIds;

  @Column(name = "user_name", nullable = false)
  @Comment("유저 이름")
  private String userName;

  @Column(name = "nick_name", nullable = false, unique = true)
  @Comment("유저 닉네임")
  private String nickName;

  @Column(name = "like_count", nullable = false)
  @Comment("좋아요 수")
  private int likeCount = 0;

  @Column(name = "matching", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
  @Comment("매칭 활성화 여부 / 기본값 = True")
  private Boolean matching;

  @Column(name = "introduction", nullable = true, columnDefinition = "LONGTEXT")
  @Comment("자기소개")
  private String introduction;

  @Column(name = "job", nullable = true, length = 20)
  @Comment("희망 직무")
  private String job;

  @Column(name = "profile_image", length = 2048)
  @Comment("프로필 사진")
  private String profileImage;

  // 회원 정보 업데이트
  public void updateUserInfo(String name, String nickname, boolean matching, String introduction,
      String job) {
    this.userName = name;
    this.nickName = nickname;
    this.matching = matching;
    this.introduction = introduction;
    this.job = job;
  }
}
