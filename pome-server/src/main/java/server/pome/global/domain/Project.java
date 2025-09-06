package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "project_name", nullable = true)
    @Comment("프로젝트명")
    private String projectName;

    @Column(name = "project_start_at", nullable = true)
    @Comment("시작일")
    private LocalDate projectStartAt;

    @Column(name = "project_end_at", nullable = true)
    @Comment("마감일")
    private LocalDate projectEndAt;

    @Column(name = "project_role", nullable = true)
    @Comment("프로젝트 역할")
    private String projectRole;

    @Column(name = "project_description", nullable = true)
    @Comment("프로젝트 설명")
    private String projectDescription;

    @Column(name = "project_award", nullable = true)
    @Comment("프로젝트 성과")
    private String projectAward;

    // 프로젝트 정보 업데이스
    public void UpdateProject(String projectName, LocalDate projectStartAt, LocalDate projectEndAt, String projectRole, String projectDescription, String projectAward) {
        this.projectName = projectName;
        this.projectStartAt = projectStartAt;
        this.projectEndAt = projectEndAt;
        this.projectRole = projectRole;
        this.projectDescription = projectDescription;
        this.projectAward = projectAward;
    }
}
