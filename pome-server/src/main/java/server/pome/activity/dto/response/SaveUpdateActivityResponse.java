package server.pome.activity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Activity;
import server.pome.global.domain.Attachment;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateActivityResponse {

  @Schema(description = "대내외활동 블록 ID", example = "1")
  private Long blockId;

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

  @Schema(description = "파일")
  private FileResponse file;

  public static SaveUpdateActivityResponse from(Activity activity, Optional<Attachment> attachment) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return SaveUpdateActivityResponse.builder()
        .blockId(activity.getId())
        .activityName(activity.getActivityName())
        .activityRole(activity.getActivityRole())
        .activityStartAt(activity.getActivityStartAt())
        .activityEndAt(activity.getActivityEndAt())
        .result(activity.getResult())
        .file(file)
        .build();
  }
}
