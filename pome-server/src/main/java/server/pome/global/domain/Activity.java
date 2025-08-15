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
@Table(name = "activities")
public class Activity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "activity_name", nullable = true)
    @Comment("활동명")
    private String activityName;

    @Column(name = "activity_role", nullable = true)
    @Comment("역할")
    private String activityRole;

    @Column(name = "activity_start_at", nullable = true)
    @Comment("활동 시작일")
    private LocalDate activityStartAt;

    @Column(name = "activity_end_at", nullable = true)
    @Comment("활동 마감일")
    private LocalDate activityEndAt;

    @Column(name = "result", nullable = true)
    @Comment("성과")
    private String result;
}
