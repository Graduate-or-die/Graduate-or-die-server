package server.pome.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import lombok.NoArgsConstructor;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.Project;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SaveProjectRequest {

    @Schema(description = "프로젝트명", example = "졸업프로젝트")
    private String projectName;

    @Schema(description = "시작일", example = "2025-09-01")
    private LocalDate projectStartAt;

    @Schema(description = "마감일", example = "2025-12-01")
    private LocalDate projectEndAt;

    @Schema(description = "사용 기술 스택 및 역할", example = "백엔드")
    private String projectRole;

    @Schema(description = "프로젝트 설명", example = "졸업하기 위한 앱 서비스")
    private String projectDescription;

    @Schema(description = "프로젝트 성과", example = "졸업가능상태")
    private String projectAward;

    public Project toEntity(Portfolio portfolio) {
        return Project.builder()
                .portfolio(portfolio)
                .projectName(projectName)
                .projectStartAt(projectStartAt)
                .projectEndAt(projectEndAt)
                .projectRole(projectRole)
                .projectDescription(projectDescription)
                .projectAward(projectAward)
                .build();
    }

}
