package server.pome.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.jwt.dto.request.DevCreateUserRequest;
import server.pome.jwt.dto.response.DevCreateUserResponse;
import server.pome.jwt.dto.response.UserLoginResponse;
import server.pome.jwt.service.DevAuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/dev")
@ConditionalOnProperty(prefix = "app.dev-auth", name = "enabled", havingValue = "true")
public class DevAuthController {

    private final DevAuthService devAuthService;

    @GetMapping("/access-token")
    public ResponseEntity<BaseResponse<UserLoginResponse>> generateTestAccessToken(
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(BaseResponse.success(devAuthService.generateTestAccessToken(userId)));
    }

    @PostMapping("/users")
    public ResponseEntity<BaseResponse<DevCreateUserResponse>> createFakeUser(
            @RequestBody(required = false) DevCreateUserRequest request
    ) {
        return ResponseEntity.ok(BaseResponse.success(devAuthService.createMockUser(request)));
    }
}
