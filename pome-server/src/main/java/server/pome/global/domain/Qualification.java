package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "qualifications")
public class Qualification extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "qualification_name", nullable = true)
    @Comment("자격증명")
    private String qualificationName;

    @Column(name = "qualification_organization", nullable = true)
    @Comment("발급기관")
    private String qualificationOrganization;

    @Column(name = "qualification_start_at", nullable = true)
    @Comment("취득일자")
    private LocalDate qualificationStartAt;

    @Column(name = "qualification_end_at", nullable = true)
    @Comment("만료일자")
    private LocalDate qualificationEndAt;

    @Column(name = "has_qualification_end_at", nullable = false)
    @Comment("만료일 여부")
    private boolean hasQualificationEndAt = true;

    @Column(name = "score", nullable = true)
    @Comment("등급/점수")
    private int score;

    // DB 저장 전에 해당 메서드 항상 호출
    @PrePersist
    @PreUpdate
    private void syncExpirationFlag() {
        if (!hasQualificationEndAt) {
            if (qualificationEndAt != null) {
                hasQualificationEndAt = true;
            }
            qualificationEndAt = null;
        }
    }

    public void setExpiration(LocalDate qualificationEndAt) {
        this.hasQualificationEndAt = true;
        this.qualificationEndAt = qualificationEndAt;
    }

    public void setNoExpiration() {
        this.hasQualificationEndAt = false;
        this.qualificationEndAt = null;
    }

    public void updateQualification(String qualificationName, String qualificationOrganization,  LocalDate qualificationStartAt, LocalDate qualificationEndAt, boolean hasQualificationEndAt, int score) {
        this.qualificationName = qualificationName;
        this.qualificationOrganization = qualificationOrganization;
        this.qualificationStartAt = qualificationStartAt;
        this.qualificationEndAt = qualificationEndAt;
        this.hasQualificationEndAt = hasQualificationEndAt;
        this.score = score;
    }

}

