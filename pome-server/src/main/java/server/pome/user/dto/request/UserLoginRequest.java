package server.pome.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest (

  @Schema(title = "아이디", example = "test")
  @NotBlank(message = "아이디를 입력해주세요.")
  String loginId

){}

