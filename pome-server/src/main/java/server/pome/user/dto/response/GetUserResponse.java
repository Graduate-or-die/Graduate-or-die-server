package server.pome.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.pome.global.domain.User;

import java.util.List;

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
  @Schema(description = "포트폴리오 태그 목록", example = "[\"대학재학생\", \"IT\", \"개발자\"]", requiredMode = RequiredMode.NOT_REQUIRED)
  private List<String> tags;
  @Schema(description = "매칭 활성화 여부", example = "true", requiredMode = RequiredMode.REQUIRED)
  private boolean matching;
  @Schema(description = "자기소개", example = "저와 개발자 포트폴리오 쌓으실 분 구해요!", requiredMode = RequiredMode.NOT_REQUIRED)
  private String introduction;
  @Schema(description = "희망 직무", example = "프론트엔드 개발자", requiredMode = RequiredMode.NOT_REQUIRED)
  private String job;
  @Schema(description = "프로필 사진", example = "https://pome-bucket.s3.ap-northeast-2.amazonaws.com/profile/배드바츠마루.jpg", requiredMode = RequiredMode.NOT_REQUIRED)
  private String profileImage;

  public static GetUserResponse from(User user) {

    return GetUserResponse.builder()
        .userId(user.getId())
        .userName(user.getUserName())
        .nickName(user.getNickName())
        .likeCount(user.getLikeCount())
        .tags(user.getTags())
        .matching(user.getMatching())
        .introduction(user.getIntroduction())
        .job(user.getJob())
            .profileImage(user.getProfileImage())
        .build();
  }
}
