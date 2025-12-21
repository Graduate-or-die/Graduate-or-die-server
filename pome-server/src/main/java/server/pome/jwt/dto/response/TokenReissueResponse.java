package server.pome.jwt.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenReissueResponse {
    private String accessToken;
    private String refreshToken;
}
