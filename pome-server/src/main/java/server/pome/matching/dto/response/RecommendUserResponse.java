package server.pome.matching.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;
import server.pome.file.FileDownloadUrls;
import server.pome.global.domain.User;

public record RecommendUserResponse(
    @Schema(description = "추천 유저 ID", example = "2", requiredMode = RequiredMode.REQUIRED)
    Long userId,

    @Schema(description = "추천 유저 닉네임", example = "포메메이트", requiredMode = RequiredMode.REQUIRED)
    String nickName,

    @Schema(
        description = "추천 유저 프로필 이미지 다운로드 URL",
        example = "/files/profile/2",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    String profileImage,

    @Schema(description = "추천 유저 좋아요 수", example = "12", requiredMode = RequiredMode.REQUIRED)
    int likeCount,

    @Schema(description = "해당 추천 회원에게 좋아요를 눌렀는지 여부", example = "true",
        requiredMode = RequiredMode.REQUIRED)
    boolean liked,

    @Schema(
        description = "추천 유저 태그 목록",
        example = "[\"#프론트엔드개발자\", \"#대기업러버\", \"#협업중심\"]",
        requiredMode = RequiredMode.REQUIRED
    )
    List<String> tags,

    @Schema(
        description = "추천 유저 자기소개",
        example = "협업과 사용자 경험을 중요하게 생각하는 프론트엔드 개발자입니다.",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    String introduction,

    @Schema(
        description = "추천 유저 희망 직무",
        example = "프론트엔드 개발자",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    String job,

    @Schema(description = "유사도 퍼센트", example = "76", requiredMode = RequiredMode.REQUIRED)
    int similarityPercent
) {

  public static RecommendUserResponse from(User user, boolean liked, int similarityPercent) {
    return new RecommendUserResponse(
        user.getId(),
        user.getNickName(),
        buildProfileImageUrl(user),
        user.getLikeCount(),
        liked,
        user.getTags(),
        user.getIntroduction(),
        user.getJob(),
        similarityPercent
    );
  }

  private static String buildProfileImageUrl(User user) {
    if (user.getProfileImage() == null || user.getProfileImage().isBlank()) {
      return null;
    }
    return FileDownloadUrls.profileImage(user.getId());
  }
}
