package server.pome.education.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import server.pome.global.domain.Education;

public record GetEducationListResponse(
  @Schema(description = "학교", example = "숙명여자대학교")
  String school,

  @Schema(description = "전공", example = "컴퓨터과학전공")
  String major,

  @Schema(description = "학위", example = "학사")
  String degree
) {

  public static GetEducationListResponse from(Education education) {
    return new GetEducationListResponse(
        education.getSchool(),
        education.getMajor(),
        education.getDegree()
    );
  }
}
