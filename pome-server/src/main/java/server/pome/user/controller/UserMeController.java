package server.pome.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.User;

@RestController
@RequestMapping("/users")
public class UserMeController {

    /**
     * JWT 인증 확인 + 내 정보 조회
     * GET /users/me
     * Header: Authorization: Bearer {서비스 accessToken}
     */
    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok((User) authentication.getPrincipal());
    }

    /**
     * 위와 동일 기능 (더 간단한 방식)
     * GET /users/me2
     */
    @GetMapping("/me2")
    public ResponseEntity<User> me2(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(user);
    }
}