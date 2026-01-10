package server.pome.jwt.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KakaoTokenResponse {

    private final String tokenType;
    private final String accessToken;
    private final Integer expiresIn;
    private final String refreshToken;
    private final Integer refreshTokenExpiresIn;
    private final String scope;

    @JsonCreator
    public KakaoTokenResponse(
            @JsonProperty("token_type")
            @Schema(description = "토큰 타입 (Bearer)")
            String tokenType,

            @JsonProperty("access_token")
            @Schema(description = "카카오 액세스 토큰")
            String accessToken,

            @JsonProperty("expires_in")
            @Schema(description = "액세스 토큰 만료 시간 (초)")
            Integer expiresIn,

            @JsonProperty("refresh_token")
            @Schema(description = "카카오 리프레시 토큰")
            String refreshToken,

            @JsonProperty("refresh_token_expires_in")
            @Schema(description = "리프레시 토큰 만료 시간 (초)")
            Integer refreshTokenExpiresIn,

            @JsonProperty("scope")
            @Schema(description = "사용자에게 동의받은 권한 범위")
            String scope
    ) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.scope = scope;
    }
}
