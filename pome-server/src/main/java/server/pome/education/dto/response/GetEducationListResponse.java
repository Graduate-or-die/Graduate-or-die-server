package server.pome.education.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Education;

public record GetEducationListResponse(
  @Schema(description = "학력 블록 ID", example = "1")
  Long blockId,

  @Schema(description = "학교", example = "홍익대학교")
  String school,

  @Schema(description = "전공", example = "컴퓨터과학전공")
  String major,

  @Schema(description = "학위", example = "학사")
  String degree,

  @Schema(description = "파일")
  FileResponse file
) {

  public static GetEducationListResponse from(Education education, Optional<Attachment> attachment) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return new GetEducationListResponse(
        education.getId(),
        education.getSchool(),
        education.getMajor(),
        education.getDegree(),
        file
    );
  }
}
