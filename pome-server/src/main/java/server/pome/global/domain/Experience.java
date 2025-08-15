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
@Table(name = "experiences")
public class Experience extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "workplace", nullable = true)
    @Comment("근무처")
    private String workplace;

    @Column(name = "spot", nullable = true)
    @Comment("직위")
    private String spot;

    @Column(name = "experience_start_at", nullable = true)
    @Comment("근무 시작일")
    private LocalDate experienceStartAt;

    @Column(name = "experience_end_at", nullable = true)
    @Comment("근무 마감일")
    private LocalDate experienceEndAt;

    // 경력 정보 업데이트
    public void updateExperience(String workplace, String spot, LocalDate startAt, LocalDate endAt) {
        this.workplace = workplace;
        this.spot = spot;
        this.experienceStartAt = startAt;
        this.experienceEndAt = endAt;
    }
}
