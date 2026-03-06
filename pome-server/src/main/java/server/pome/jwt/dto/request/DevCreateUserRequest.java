package server.pome.jwt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DevCreateUserRequest {

    @Schema(description = "사용자 이름(미입력 시 자동 생성)", example = "테스트유저")
    private String userName;

    @Schema(description = "닉네임(중복/미입력 시 자동 생성)", example = "test-nick")
    private String nickName;

    @Schema(description = "이메일(중복/미입력 시 자동 생성)", example = "test@example.com")
    private String email;

    @Schema(description = "매칭 활성화 여부(미입력 시 true)", example = "true")
    private Boolean matching;
}
