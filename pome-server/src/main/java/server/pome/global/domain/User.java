package server.pome.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.Comment;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.user.converter.StringListJsonConverter;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@Builder
public class User extends BaseEntity {

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

  @Column(name = "profile_image_url", length = 2048)
  @Comment("프로필 사진")
  private String profileImage;

  @Column(name = "kakao_id", nullable = false)
  @Comment("카카오 아이디")
  private Long kakaoId;

  @Column(name = "email", nullable = false)
  @Comment("이메일")
  private String email;

  @Convert(converter = StringListJsonConverter.class)
  @Column(name = "tags", columnDefinition = "TEXT")
  @Comment("포트폴리오 태그 목록")
  private List<String> tags = new ArrayList<>();

  @Column(name = "tags_updated_portfolio_version")
  private Long tagsUpdatedPortfolioVersion;

  // 회원 정보 업데이트
  public void updateUserInfo(String name, String nickname, boolean matching, String introduction,
      String job) {
    this.userName = name;
    this.nickName = nickname;
    this.matching = matching;
    this.introduction = introduction;
    this.job = job;
  }

  // 이미지 업데이트
  public void updateProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }

  // 포트폴리오 태그 업데이트
  public void updateTags(List<String> tags, Long version) {
    this.tags = tags;
    this.tagsUpdatedPortfolioVersion = version;
  }

  // 좋아요 수 증가
  public void addLike() {
    this.likeCount += 1;
  }

  // 좋아요 수 감소
  public void disLike() {
    this.likeCount -= 1;
  }

  public User linkKakao(Long kakaoId, String email) {
    // 이미 kakaoId가 연동되어 있으면 그대로 사용
    if (this.kakaoId != null && !this.kakaoId.equals(kakaoId)) {
      // 다른 카카오 계정이 이미 연동된 상황
      throw new BaseException(BaseResponseStatus.OAUTH_UNAUTHORIZED);
    }
    if (this.kakaoId == null) {
      this.kakaoId = kakaoId;
    }
    if ((this.email == null || this.email.isBlank()) && email != null && !email.isBlank()) {
      this.email = email;
    }
    return this;
  }
}
