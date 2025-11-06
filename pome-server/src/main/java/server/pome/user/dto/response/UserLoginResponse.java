package server.pome.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponse {

    private Long userId;
    private String userName;
    private String nickName;

    // JWT 발급 시 아래 두 필드 활성화
    // private String accessToken;
    // private String refreshToken;
}
