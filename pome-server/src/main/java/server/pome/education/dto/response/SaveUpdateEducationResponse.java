package server.pome.education.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Education;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateEducationResponse {

  @Schema(description = "학력 블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "학교", example = "숙명여자대학교")
  private String school;

  @Schema(description = "전공", example = "컴퓨터과학전공")
  private String major;

  @Schema(description = "학위", example = "학사")
  private String degree;

  @Schema(description = "파일")
  private FileResponse file;

  public static SaveUpdateEducationResponse from(Education education, Optional<Attachment> attachment) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return SaveUpdateEducationResponse.builder()
        .blockId(education.getId())
        .school(education.getSchool())
        .major(education.getMajor())
        .degree(education.getDegree())
        .file(file)
        .build();
  }
}
