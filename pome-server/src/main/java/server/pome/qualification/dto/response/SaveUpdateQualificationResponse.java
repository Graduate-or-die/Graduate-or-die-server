package server.pome.qualification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Qualification;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateQualificationResponse {

  @Schema(description = "자격증 블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "자격증명", example = "정보처리기사")
  private String qualificationName;

  @Schema(description = "발급기관", example = "한국산업인력공단")
  private String qualificationOrganization;

  @Schema(description = "취득일자", example = "2024-01-01")
  private LocalDate qualificationStartAt;

  @Schema(description = "만료일자", example = "2027-01-01")
  private LocalDate qualificationEndAt;

  @Schema(description = "만료일 여부", example = "true")
  private boolean hasQualificationEndAt;

  @Schema(description = "등급/점수", example = "1")
  private int score;

  @Schema(description = "파일")
  private FileResponse file;

  public static SaveUpdateQualificationResponse from(
      Qualification qualification,
      Optional<Attachment> attachment
  ) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return SaveUpdateQualificationResponse.builder()
        .blockId(qualification.getId())
        .qualificationName(qualification.getQualificationName())
        .qualificationOrganization(qualification.getQualificationOrganization())
        .qualificationStartAt(qualification.getQualificationStartAt())
        .qualificationEndAt(qualification.getQualificationEndAt())
        .hasQualificationEndAt(qualification.isHasQualificationEndAt())
        .score(qualification.getScore())
        .file(file)
        .build();
  }
}
