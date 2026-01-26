package server.pome.qualification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import server.pome.global.domain.Qualification;

public record GetQualificationListResponse(
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

    @Schema(description = "첨부", example = "qulificationimg/url")
    List<String> qualificationFile
) {

  public static GetQualificationListResponse from(Qualification qualification) {
    return new GetQualificationListResponse(
        qualification.getQualificationName(),
        qualification.getQualificationOrganization(),
        qualification.getQualificationStartAt(),
        qualification.getQualificationEndAt(),
        qualification.isHasQualificationEndAt(),
        qualification.getScore(),
        qualification.getQualificationFile()
    );
  }
}
