package server.pome.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Project;

public record GetProjectListResponse(
  @Schema(description = "프로젝트 블록 ID", example = "1")
  Long blockId,

  @Schema(description = "프로젝트명", example = "졸업프로젝트")
  String projectName,

  @Schema(description = "시작일", example = "2025-09-01")
  LocalDate projectStartAt,

  @Schema(description = "마감일", example = "2025-12-01")
  LocalDate projectEndAt,

  @Schema(description = "프로젝트 역할", example = "백엔드")
  String projectRole,

  @Schema(description = "프로젝트 설명", example = "졸업하기 위한 앱 서비스")
  String projectDescription,

  @Schema(description = "프로젝트 성과", example = "졸업가능상태")
  String projectAward
) {

  public static GetProjectListResponse from(Project project) {
    return new GetProjectListResponse(
        project.getId(),
        project.getProjectName(),
        project.getProjectStartAt(),
        project.getProjectEndAt(),
        project.getProjectRole(),
        project.getProjectDescription(),
        project.getProjectAward()
    );
  }
}
