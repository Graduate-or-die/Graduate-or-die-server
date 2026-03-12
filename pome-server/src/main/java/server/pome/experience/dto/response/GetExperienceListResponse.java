package server.pome.experience.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Experience;

public record GetExperienceListResponse (
  @Schema(description = "경력 블록 ID", example = "1")
  Long blockId,

  @Schema(description = "근무처", example = "네이버")
  String workplace,

  @Schema(description = "직위", example = "인턴")
  String spot,

  @Schema(description = "근무 시작일", example = "2025-01-01")
  LocalDate experienceStartAt,

  @Schema(description = "근무 마감일", example = "2025-08-01")
  LocalDate experienceEndAt,

  @Schema(description = "파일")
  FileResponse file
) {

  public static GetExperienceListResponse from(Experience experience, Optional<Attachment> attachment) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return new GetExperienceListResponse(
        experience.getId(),
        experience.getWorkplace(),
        experience.getSpot(),
        experience.getExperienceStartAt(),
        experience.getExperienceEndAt(),
        file
    );
  }
}
