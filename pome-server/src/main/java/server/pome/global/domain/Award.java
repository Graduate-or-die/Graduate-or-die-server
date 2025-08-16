package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "awards")
public class Award extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "award_name", nullable = true)
    @Comment("대회명")
    private String awardName;

    @Column(name = "award_organization", nullable = true)
    @Comment("주최기관")
    private String awardOrganization;

    @Column(name = "award_date", nullable = true)
    @Comment("수상일자")
    private LocalDate awardDate;

    @Column(name = "award_grade", nullable = true)
    @Comment("시상등급")
    private String awardGrade;

    @ElementCollection // 추후 첨부파일 관련 테이블 생성 예정 @CollectionTable
    @Column(name = "award_file", nullable = true)
    @Comment("첨부")
    private List<String> awardFile = new ArrayList<>();

    // 수상경력 정보 업데이트
    public void updateAward(String awardName, String awardOrganization, LocalDate awardDate, String awardGrade, List<String> awardFile) {
        this.awardName = awardName;
        this.awardOrganization = awardOrganization;
        this.awardDate = awardDate;
        this.awardGrade = awardGrade;
        this.awardFile = awardFile;
    }
}

