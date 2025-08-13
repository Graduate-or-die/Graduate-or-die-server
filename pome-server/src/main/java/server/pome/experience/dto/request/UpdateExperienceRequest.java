package server.pome.experience.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateExperienceRequest {
    @Schema(description = "근무처", example = "네이버")
    private String workplace;

    @Schema(description = "직위", example = "인턴")
    private String spot;

    @Schema(description = "근무 시작일", example = "2025-01-01")
    private LocalDate experienceStartAt;

    @Schema(description = "근무 마감일", example = "2025-08-01")
    private LocalDate experienceEndAt;

}
