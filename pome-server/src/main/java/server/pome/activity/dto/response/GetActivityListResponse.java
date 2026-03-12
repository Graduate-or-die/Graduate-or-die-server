package server.pome.activity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Activity;
import server.pome.global.domain.Attachment;

public record GetActivityListResponse(
  @Schema(description = "대내외활동 블록 ID", example = "1")
  Long blockId,

  @Schema(description = "활동명", example = "2025AI해커톤")
  String activityName,

  @Schema(description = "역할", example = "PM")
  String activityRole,

  @Schema(description = "활동 시작일", example = "2025-09-01")
  LocalDate activityStartAt,

  @Schema(description = "활동 마감일", example = "2025-09-02")
  LocalDate activityEndAt,

  @Schema(description = "성과", example = "우수상")
  String result,

  @Schema(description = "파일")
  FileResponse file
) {

  public static GetActivityListResponse from(Activity activity, Optional<Attachment> attachment) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return new GetActivityListResponse(
        activity.getId(),
        activity.getActivityName(),
        activity.getActivityRole(),
        activity.getActivityStartAt(),
        activity.getActivityEndAt(),
        activity.getResult(),
        file
    );
  }
}
