package server.pome.jwt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenReissueResponse {

    @Schema(description = "카카오 엑세스 토큰")
    private String accessToken;
    @Schema(description = "카카오 리프레쉬 토큰")
    private String refreshToken;

    public TokenReissueResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

    }
}
