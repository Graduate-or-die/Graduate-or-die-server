package server.pome.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateUserResponse {

  @Schema(description = "회원 ID", example = "1")
  private Long userId;

  @Schema(description = "유저 닉네임", example = "윤현서")
  private String userName;

  @Schema(description = "유저 닉네임", example = "김혜림")
  private String nickName;
}
