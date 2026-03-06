package server.pome.jwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import server.pome.global.domain.BaseResponse;
import server.pome.jwt.dto.request.KakaoLoginRequest;
import server.pome.jwt.dto.response.LoginTokensResponse;
import server.pome.jwt.dto.response.TokenReissueResponse;
import server.pome.jwt.dto.response.UserLoginResponse;
import server.pome.jwt.service.AuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.frontend.kakao-callback-url:http://localhost:3000/auth/callback}")
    private String kakaoFrontendCallbackUrl;

    @Operation(summary = "카카오 로그인", description = "카카오가 직접 리다이렉트 하는 용도")
    @GetMapping("/login")
    public ResponseEntity<Void> kakaoLoginCallback(
            @RequestParam("code") String code
    ) {
        String redirectUrl = UriComponentsBuilder.fromUriString(kakaoFrontendCallbackUrl)
                .queryParam("code", code)
                .build()
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(redirectUrl))
                .build();
    }

    // 카카오 로그인
    @Operation(summary = "카카오 로그인 (프론트 용)", description = "프론트가 code를 보내주는 용도")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserLoginResponse>> kakaoLogin(
            @Valid @RequestBody KakaoLoginRequest request
    ) {
        LoginTokensResponse tokens = authService.loginWithKakaoCode(request.getCode());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();

        UserLoginResponse body = UserLoginResponse.builder()
                .userId(tokens.getUserId())
                .userName(tokens.getUserName())
                .nickName(tokens.getNickName())
                .accessToken(tokens.getAccessToken())
                .build();

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(BaseResponse.success(body));
    }

    @Operation(summary = "refreshToken으로 accessToken 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<TokenReissueResponse>> reissue(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        TokenReissueResponse result = authService.reissue(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true) 
                .sameSite("None")
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(BaseResponse.success(result));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<TokenReissueResponse>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(BaseResponse.success(null));
    }
}
