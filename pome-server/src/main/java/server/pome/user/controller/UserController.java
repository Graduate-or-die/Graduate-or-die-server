package server.pome.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import server.pome.global.domain.User;
import server.pome.like.dto.response.LikeResponse;
import server.pome.like.service.LikeService;
import server.pome.jwt.dto.request.KakaoLoginRequest;
import server.pome.user.dto.request.UpdateUserRequest;
import server.pome.user.dto.response.GetUserResponse;
import server.pome.jwt.dto.response.LoginTokensResponse;
import server.pome.user.dto.response.UpdateUserResponse;
import server.pome.jwt.dto.response.UserLoginResponse;
import server.pome.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "회원 API")
public class UserController {

  private final UserService userService;
  private final LikeService likeService;

  // 회원 정보 조회
  @Operation(summary = "회원 정보 조회")
  @GetMapping("/mypage")
  public ResponseEntity<BaseResponse<GetUserResponse>> getUserInfo(
          Authentication authentication
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    GetUserResponse result = userService.getUserInfo(userId);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  // 회원 정보 수정
  @Operation(summary = "회원 정보 수정")
  @PatchMapping("/mypage")
  public ResponseEntity<BaseResponse<UpdateUserResponse>> updateUserInfo(
          Authentication authentication,
      @Valid @RequestBody UpdateUserRequest updateUserRequest
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    UpdateUserResponse result = userService.updateUserInfo(userId, updateUserRequest);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  // 회원 검색
  @Operation(summary = "메이트 검색")
  @Parameter(name = "name", description = "검색할 메이트 닉네임", required = true)
  @GetMapping("search")
  public ResponseEntity<BaseResponse<GetUserResponse>> searchUser(
          Authentication authentication,
      @RequestParam("name") String name
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    GetUserResponse result = userService.searchUser(userId, name);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  @Operation(summary = "유저 좋아요")
  @Parameter(name = "mateId", description = "좋아요를 누른 대상 유저 ID", required = true)
  @PostMapping("/likes/{mateId}")
  public ResponseEntity<BaseResponse<LikeResponse>> like (
      @PathVariable Long mateId,
      Authentication authentication
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    LikeResponse result = likeService.like(mateId, userId);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  @Operation(summary = "유저 좋아요 취소")
  @Parameter(name = "mateId", description = "좋아요 취소 당한 대상 유저 ID", required = true)
  @DeleteMapping("/likes/{mateId}")
  public ResponseEntity<BaseResponse<LikeResponse>> unlike (
      @PathVariable Long mateId,
      Authentication authentication
  ) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    LikeResponse result = likeService.unlike(mateId, userId);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

}
