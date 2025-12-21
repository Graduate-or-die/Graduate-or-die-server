package server.pome.jwt.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginTokensResponse {

    private Long userId;
    private String userName;
    private String nickName;

    private String accessToken;
    private String refreshToken;
}
