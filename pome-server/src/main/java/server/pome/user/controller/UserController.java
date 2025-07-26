package server.pome.user.controller;

import com.mysql.cj.x.protobuf.Mysqlx.Ok;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.user.dto.request.UserLoginRequest;
import server.pome.user.dto.response.UserLoginResponse;
import server.pome.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "회원 API")
public class UserController {
  private final UserService userService;

  // 로그인
  @Operation(summary = "로그인")
  @PostMapping("/login")
  public ResponseEntity<BaseResponse<UserLoginResponse>> login(
      @Valid @RequestBody UserLoginRequest userLoginRequest
  ) {
    UserLoginResponse result = userService.login(userLoginRequest);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  // 회원 정보 조회

  // 회원 정보 수정

}
