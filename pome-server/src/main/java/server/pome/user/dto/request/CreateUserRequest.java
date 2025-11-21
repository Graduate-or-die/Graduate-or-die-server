package server.pome.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateUserRequest {

  @Schema(description = "유저 닉네임", example = "윤현서")
  private String userName;

  @Schema(description = "유저 닉네임", example = "김혜림")
  private String nickName;

  //@Schema(description = "비밀번호", example = "1234")
  //private String password;
}
