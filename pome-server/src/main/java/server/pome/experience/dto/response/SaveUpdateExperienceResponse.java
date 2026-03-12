package server.pome.experience.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Experience;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateExperienceResponse {

  @Schema(description = "경력 블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "근무처", example = "네이버")
  private String workplace;
  
  @Schema(description = "직위", example = "인턴")
  private String spot;
  
  @Schema(description = "근무 시작일", example = "2025-01-01")
  private LocalDate experienceStartAt;

  @Schema(description = "근무 마감일", example = "2025-08-01")
  private LocalDate experienceEndAt;

  @Schema(description = "파일")
  private FileResponse file;

  public static SaveUpdateExperienceResponse from(Experience experience, Optional<Attachment> attachment) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return SaveUpdateExperienceResponse.builder()
        .blockId(experience.getId())
        .workplace(experience.getWorkplace())
        .spot(experience.getSpot())
        .experienceStartAt(experience.getExperienceStartAt())
        .experienceEndAt(experience.getExperienceEndAt())
        .file(file)
        .build();
  }
}
