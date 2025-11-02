package server.pome.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "users")
public class User extends BaseEntity {

  @Column(name = "password", nullable = false)
  @Comment("비밀번호")
  private String password;

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

  // 좋아요 수 증가
  public void addLike() {
    this.likeCount += 1;
  }

  // 좋아요 수 감소
  public void disLike() {
    this.likeCount -= 1;
  }
}
