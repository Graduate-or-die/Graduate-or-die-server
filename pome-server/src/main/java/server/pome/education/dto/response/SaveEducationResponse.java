package server.pome.education.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SaveEducationResponse {

    @Schema(description = "학력 ID", example = "1")
    private Long educationId;

    @Schema(description = "학교", example = "숙명여자대학교")
    private String school;

    @Schema(description = "전공", example = "컴퓨터과학전공")
    private String major;

    @Schema(description = "학위", example = "학사")
    private String degree;
}
