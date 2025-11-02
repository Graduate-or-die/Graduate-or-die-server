package server.pome.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.global.domain.User;

@Getter
@Builder
@AllArgsConstructor
public class UpdateUserResponse {
  @Schema(description = "유저 ID", example = "1")
  private Long userId;
  @Schema(description = "유저 이름", example = "윤현서")
  private String userName;
  @Schema(description = "유저 닉네임", example = "김혜림")
  private String nickName;
  @Schema(description = "매칭 활성화 여부", example = "true")
  private Boolean matching;
  @Schema(description = "자기소개", example = "저와 개발자 포트폴리오 쌓으실 분 구해요!")
  private String introduction;
  @Schema(description = "희망 직무", example = "프론트엔드 개발자")
  private String job;
  // TODO: 프로필 사진 추가

  public static UpdateUserResponse from(User user) {
    return UpdateUserResponse.builder()
        .userId(user.getId())
        .userName(user.getUserName())
        .nickName(user.getNickName())
        .matching(user.getMatching())
        .introduction(user.getIntroduction())
        .job(user.getJob())
        .build();
  }
}
