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
@Table(name = "qualifications")
public class Qualification extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id",nullable = false)
    private Portfolio portfolio;

    @Column(name = "qualification name", nullable = true)
    @Comment("자격증명")
    private String qualificationName;

    @Column(name = "qualification organization", nullable = true)
    @Comment("발급기관")
    private String qualificationOrganization;

    @Column(name = "qualification start_date", nullable = true)
    @Comment("취득일자")
    private LocalDate qualificationStartDate;

    @Column(name = "qualification end_date", nullable = true)
    @Comment("만료일자")
    private LocalDate qualificationEndDate;

    @Column(name = "score", nullable = true)
    @Comment("등급/점수")
    private int score;

    @ElementCollection // 추후 첨부 파일 관련 테이블 생성 예정  @CollectionTable
    @Column(name = "qulification_file", nullable = true)
    @Comment("첨부")
    private List<String> qualificationFile = new ArrayList<>();
}
