package server.pome.jwt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevCreateUserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "테스트유저")
    private String userName;

    @Schema(description = "닉네임", example = "test-nick")
    private String nickName;

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @Schema(description = "내부 테스트용 kakaoId", example = "123456789012345")
    private Long kakaoId;

    @Schema(description = "태그 목록")
    private List<String> tags;

    @Schema(description = "생성 직후 발급된 access token(JWT)")
    private String accessToken;
}
