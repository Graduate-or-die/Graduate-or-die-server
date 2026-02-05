package server.pome.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateUserRequest {
  @Schema(description = "유저 이름", example = "윤현서")
  private String userName;
  @Schema(description = "유저 닉네임", example = "김혜림")
  private String nickName;
  @Schema(description = "자기소개", example = "저와 개발자 포트폴리오 쌓으실 분 구해요!")
  private String introduction;
  @Schema(description = "희망 직무", example = "프론트엔드 개발자")
  private String job;
  @Schema(description = "매칭 활성화 여부", example = "true")
  private Boolean matching;

}
