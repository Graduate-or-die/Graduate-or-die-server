package server.pome.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.user.dto.request.CreateUserRequest;
import server.pome.user.dto.request.UpdateUserRequest;
import server.pome.user.dto.request.UserLoginRequest;
import server.pome.user.dto.response.CreateUserResponse;
import server.pome.user.dto.response.GetUserResponse;
import server.pome.user.dto.response.UpdateUserResponse;
import server.pome.user.dto.response.UserLoginResponse;
import server.pome.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "회원 API")
public class UserController {

  private final UserService userService;

  // 조회, 수정 API 테스트용 임시 회원가입 API
  @Operation(summary = "회원가입")
  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<CreateUserResponse>> createUser(
      @Valid @RequestBody CreateUserRequest request) {
    CreateUserResponse response = userService.createUser(request);
    return ResponseEntity.ok(BaseResponse.success(response));
  }

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
  @Operation(summary = "회원 정보 조회")
  @Parameter(name = "userId", description = "회원 ID", required = true)
  @GetMapping("/mypage/{userId}")
  public ResponseEntity<BaseResponse<GetUserResponse>> getUserInfo(
      @PathVariable Long userId
  ) {
    GetUserResponse result = userService.getUserInfo(userId);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  // 회원 정보 수정
  @Operation(summary = "회원 정보 수정")
  @Parameter(name = "userId", description = "회원 ID", required = true)
  @PatchMapping("/mypage/{userId}")
  public ResponseEntity<BaseResponse<UpdateUserResponse>> updateUserInfo(
      @PathVariable Long userId,
      @Valid @RequestBody UpdateUserRequest updateUserRequest
  ) {
    UpdateUserResponse result = userService.updateUserInfo(userId, updateUserRequest);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
