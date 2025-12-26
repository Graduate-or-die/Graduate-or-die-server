package server.pome.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "카카오 사용자 이름", example = "김혜림")
    private String userName;

    @Schema(description = "카카오 닉네임", example = "혜리미")
    private String nickName;

    @Schema(description = "서비스 엑세스 토큰 (JWT)", example = "혜리미")
    private String accessToken;
}
