package server.pome.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.like.dto.response.LikeResponse;
import server.pome.like.service.LikeService;
import server.pome.user.dto.request.CreateUserRequest;
import server.pome.user.dto.request.KakaoLoginRequest;
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
  private final LikeService likeService;

  // 카카오 로그인 (인가코드 → JWT)
  @Operation(summary = "카카오 로그인")
  @PostMapping("/login")
  public ResponseEntity<BaseResponse<UserLoginResponse>> kakaoLogin(
          @Valid @RequestBody KakaoLoginRequest request
  ) {
    // Todo 1. code → 카카오 토큰 교환
    // Todo 2. access_token → 카카오 유저정보 조회
    // Todo 3. DB 매핑
    // Todo 4. 서비스 JWT 발급 후 반환
    UserLoginResponse result = userService.loginWithKakaoCode(request.getCode());
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

  // 회원 검색
  @Operation(summary = "메이트 검색")
  @Parameters({
      @Parameter(name = "userId", description = "회원 ID", required = true),
      @Parameter(name = "name", description = "검색할 메이트 닉네임", required = true)
  })
  @GetMapping("search/{userId}")
  public ResponseEntity<BaseResponse<GetUserResponse>> searchUser(
      @PathVariable Long userId,
      @RequestParam("name") String name
  ) {
    GetUserResponse result = userService.searchUser(userId, name);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  @Operation(summary = "유저 좋아요")
  @Parameters({
      @Parameter(name = "mateId", description = "좋아요를 누른 대상 유저 ID", required = true),
      @Parameter(name = "userId", description = "좋아요를 누른 유저 ID", required = true)
  })
  @PostMapping("/likes/{mateId}/{userId}")
  public ResponseEntity<BaseResponse<LikeResponse>> like (
      @PathVariable Long mateId,
      @PathVariable Long userId
  ) {
    LikeResponse result = likeService.like(mateId, userId);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  @Operation(summary = "유저 좋아요 취소")
  @Parameters({
      @Parameter(name = "mateId", description = "좋아요 취소 당한 대상 유저 ID", required = true),
      @Parameter(name = "userId", description = "좋아요 취소를 누른 유저 ID", required = true)
  })
  @DeleteMapping("/likes/{mateId}/{userId}")
  public ResponseEntity<BaseResponse<LikeResponse>> unlike (
      @PathVariable Long mateId,
      @PathVariable Long userId
  ) {
    LikeResponse result = likeService.unlike(mateId, userId);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
