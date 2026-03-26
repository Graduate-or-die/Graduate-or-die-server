package server.pome.mate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.pome.file.FileDownloadUrls;
import server.pome.global.domain.Mate;
import server.pome.global.domain.User;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GetMateRequestResponse {

  @Schema(description = "메이트 신청자 ID", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long mateId;

  @Schema(description = "메이트 신청자 닉네임", example = "구준회", requiredMode = RequiredMode.REQUIRED)
  private String mateNickname;

  @Schema(description = "메이트 신청자 프로필 이미지 다운로드 URL", example = "/files/profile/1",
      requiredMode = RequiredMode.NOT_REQUIRED)
  private String mateProfileImage;

  @Schema(description = "메이트 신청자 자기소개", example = "백엔드 개발 같이 하실 분 찾고 있어요.",
      requiredMode = RequiredMode.NOT_REQUIRED)
  private String mateIntroduction;

  public static GetMateRequestResponse from(Mate mate) {
    User mateUser = mate.getFromUser();
    return GetMateRequestResponse.builder()
        .mateId(mateUser.getId())
        .mateNickname(mateUser.getNickName())
        .mateProfileImage(buildProfileImageUrl(mateUser))
        .mateIntroduction(mateUser.getIntroduction())
        .build();
  }

  private static String buildProfileImageUrl(User user) {
    if (user.getProfileImage() == null || user.getProfileImage().isBlank()) {
      return null;
    }
    return FileDownloadUrls.profileImage(user.getId());
  }
}
