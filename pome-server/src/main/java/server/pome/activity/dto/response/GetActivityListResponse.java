package server.pome.activity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import server.pome.global.domain.Activity;

public record GetActivityListResponse(
  @Schema(description = "활동명", example = "2025AI해커톤")
  String activityName,

  @Schema(description = "역할", example = "PM")
  String activityRole,

  @Schema(description = "활동 시작일", example = "2025-09-01")
  LocalDate activityStartAt,

  @Schema(description = "활동 마감일", example = "2025-09-02")
  LocalDate activityEndAt,

  @Schema(description = "성과", example = "우수상")
  String result
) {

  public static GetActivityListResponse from(Activity activity) {
    return new GetActivityListResponse(
        activity.getActivityName(),
        activity.getActivityRole(),
        activity.getActivityStartAt(),
        activity.getActivityEndAt(),
        activity.getResult()
    );
  }
}
