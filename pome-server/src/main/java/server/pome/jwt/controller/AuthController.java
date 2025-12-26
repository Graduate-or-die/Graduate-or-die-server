package server.pome.jwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.jwt.dto.response.TokenReissueResponse;
import server.pome.jwt.service.AuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // refreshToken으로 accessToken 재발급
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

    // 로그아웃
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
