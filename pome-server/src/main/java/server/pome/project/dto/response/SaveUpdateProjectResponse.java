package server.pome.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Project;

@Getter
@Builder
@AllArgsConstructor
public class SaveUpdateProjectResponse {

  @Schema(description = "프로젝트 블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "프로젝트명", example = "졸업프로젝트")
  private String projectName;

  @Schema(description = "시작일", example = "2025-09-01")
  private LocalDate projectStartAt;

  @Schema(description = "마감일", example = "2025-12-01")
  private LocalDate projectEndAt;

  @Schema(description = "프로젝트 역할", example = "백엔드")
  private String projectRole;

  @Schema(description = "프로젝트 설명", example = "졸업하기 위한 앱 서비스")
  private String projectDescription;

  @Schema(description = "프로젝트 성과", example = "졸업가능상태")
  private String projectAward;

  @Schema(description = "파일")
  private FileResponse file;

  public static SaveUpdateProjectResponse from(Project project, Optional<Attachment> attachment) {
    FileResponse file = attachment
        .map(FileResponse::from)
        .orElse(null);

    return SaveUpdateProjectResponse.builder()
        .blockId(project.getId())
        .projectName(project.getProjectName())
        .projectStartAt(project.getProjectStartAt())
        .projectEndAt(project.getProjectEndAt())
        .projectRole(project.getProjectRole())
        .projectDescription(project.getProjectDescription())
        .projectAward(project.getProjectAward())
        .file(file)
        .build();
  }
}
