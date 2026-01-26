package server.pome.jwt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginTokensResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "카카오 사용자 이름", example = "김혜림")
    private String userName;
    @Schema(description = "카카오 닉네임", example = "혜리미")
    private String nickName;
    @Schema(description = "카카오 엑세스 토큰")
    private String accessToken;
    @Schema(description = "카카오 리프레쉬 토큰")
    private String refreshToken;
}
