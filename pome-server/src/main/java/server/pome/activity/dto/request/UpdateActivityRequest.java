package server.pome.activity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateActivityRequest {

    @Schema(description = "활동명", example = "2025AI해커톤")
    private String activityName;

    @Schema(description = "역할", example = "PM")
    private String activityRole;

    @Schema(description = "활동 시작일", example = "2025-09-01")
    private LocalDate activityStartAt;

    @Schema(description = "활동 마감일", example = "2025-09-02")
    private LocalDate activityEndAt;

    @Schema(description = "성과", example = "우수상")
    private String result;
}
