package server.pome.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.pome.file.FileDownloadUrls;
import server.pome.global.domain.User;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GetUserResponse {

  @Schema(description = "회원 ID", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long userId;

  @Schema(description = "이름", example = "윤현서", requiredMode = RequiredMode.REQUIRED)
  private String userName;

  @Schema(description = "닉네임", example = "김혜림", requiredMode = RequiredMode.REQUIRED)
  private String nickName;

  @Schema(description = "좋아요 수", example = "57", requiredMode = RequiredMode.REQUIRED)
  private int likeCount;

  @Schema(
      description = "태그 목록",
      example = "[\"대학재학생\", \"IT\", \"개발자\"]",
      requiredMode = RequiredMode.NOT_REQUIRED
  )
  private List<String> tags;

  @Schema(description = "매칭 활성화 여부", example = "true", requiredMode = RequiredMode.REQUIRED)
  private boolean matching;

  @Schema(description = "자기소개", example = "저와 개발자 포트폴리오 쌓으실 분 구해요!", requiredMode = RequiredMode.NOT_REQUIRED)
  private String introduction;

  @Schema(description = "희망 직무", example = "프론트엔드 개발자", requiredMode = RequiredMode.NOT_REQUIRED)
  private String job;

  @Schema(description = "프로필 이미지 다운로드 URL", example = "/files/profile", requiredMode = RequiredMode.NOT_REQUIRED)
  private String profileImage;

  public static GetUserResponse from(User user) {
    return from(user, false);
  }

  public static GetUserResponse from(User user, boolean targetUserProfileImage) {
    return GetUserResponse.builder()
        .userId(user.getId())
        .userName(user.getUserName())
        .nickName(user.getNickName())
        .likeCount(user.getLikeCount())
        .tags(user.getTags())
        .matching(user.getMatching())
        .introduction(user.getIntroduction())
        .job(user.getJob())
        .profileImage(buildProfileImageUrl(user, targetUserProfileImage))
        .build();
  }

  private static String buildProfileImageUrl(User user, boolean targetUserProfileImage) {
    if (user.getProfileImage() == null || user.getProfileImage().isBlank()) {
      return null;
    }
    if (targetUserProfileImage) {
      return FileDownloadUrls.profileImage(user.getId());
    }
    return FileDownloadUrls.profileImage();
  }
}
