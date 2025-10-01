package server.pome.experience.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.global.domain.Experience;

import java.time.LocalDate;


@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateExperienceResponse {

    @Schema(description = "경력 ID", example = "1")
    private Long experienceId;

    @Schema(description = "근무처", example = "네이버")
    private String workplace;

    @Schema(description = "직위", example = "인턴")
    private String spot;

    @Schema(description = "근무 시작일", example = "2025-01-01")
    private LocalDate experienceStartAt;

    @Schema(description = "근무 마감일", example = "2025-08-01")
    private LocalDate experienceEndAt;

    public static SaveUpdateExperienceResponse from(Experience e) {
        return SaveUpdateExperienceResponse.builder()
                .experienceId(e.getId())
                .workplace(e.getWorkplace())
                .spot(e.getSpot())
                .experienceStartAt(e.getExperienceStartAt())
                .experienceEndAt(e.getExperienceEndAt())
                .build();
    }
}
