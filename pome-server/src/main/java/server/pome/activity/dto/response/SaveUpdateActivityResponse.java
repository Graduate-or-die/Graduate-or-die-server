package server.pome.activity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.global.domain.Activity;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateActivityResponse {

    @Schema(description = "대내와활동 ID", example = "1")
    private Long activityId;

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

    public static SaveUpdateActivityResponse from(Activity activity) {
        return SaveUpdateActivityResponse.builder()
                .activityId(activity.getId())
                .activityName(activity.getActivityName())
                .activityRole(activity.getActivityRole())
                .activityStartAt(activity.getActivityStartAt())
                .activityEndAt(activity.getActivityEndAt())
                .result(activity.getResult())
                .build();
    }
}
