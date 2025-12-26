package server.pome.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest {

    @Schema(description = "카카오 OAuth 인가 코드 (authorization code)")
    private String code; // 인가코드 매핑
}
