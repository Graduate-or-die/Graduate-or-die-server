package server.pome.jwt.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenReissueResponse {
    private String accessToken;
    private String refreshToken;

    public TokenReissueResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
