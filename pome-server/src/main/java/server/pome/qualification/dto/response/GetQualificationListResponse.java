package server.pome.qualification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Qualification;

public record GetQualificationListResponse(
  @Schema(description = "자격증 블록 ID", example = "1")
  Long blockId,

  @Schema(description = "자격증명", example = "정보처리기사")
  String qualificationName,

  @Schema(description = "발급기관", example = "한국산업인력공단")
  String qualificationOrganization,

  @Schema(description = "취득일자", example = "2024-01-01")
  LocalDate qualificationStartAt,

  @Schema(description = "만료일자", example = "2027-01-01")
  LocalDate qualificationEndAt,

  @Schema(description = "만료일 여부", example = "true")
  boolean hasQualificationEndAt,

  @Schema(description = "등급/점수", example = "1")
  int score,

  @Schema(description = "파일")
  FileResponse file

) {

  public static GetQualificationListResponse from(
      Qualification qualification,
      Optional<Attachment> attachment
  ) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return new GetQualificationListResponse(
        qualification.getId(),
        qualification.getQualificationName(),
        qualification.getQualificationOrganization(),
        qualification.getQualificationStartAt(),
        qualification.getQualificationEndAt(),
        qualification.isHasQualificationEndAt(),
        qualification.getScore(),
        file
    );
  }
}
