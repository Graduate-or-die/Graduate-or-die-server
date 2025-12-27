package server.pome.award.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import server.pome.global.domain.Award;

public record GetAwardListResponse(
  @Schema(description = "대회명", example = "빨리먹기대회")
  String awardName,

  @Schema(description = "주최기관", example = "숙명캠퍼스다운사업단")
  String awardOrganization,

  @Schema(description = "수상일자", example = "2025-09-01")
  LocalDate awardAt,

  @Schema(description = "시상등급", example = "대상")
  String awardGrade,

  @Schema(description = "첨부", example = "awardimg/url")
  List<String> awardFile
) {

  public static GetAwardListResponse from(Award award) {
    return new GetAwardListResponse(
        award.getAwardName(),
        award.getAwardOrganization(),
        award.getAwardAt(),
        award.getAwardGrade(),
        award.getAwardFile()
    );
  }
}
