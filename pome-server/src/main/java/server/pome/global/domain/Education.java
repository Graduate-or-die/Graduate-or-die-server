package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="education")
public class Education extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "school", nullable = true)
    @Comment("학교")
    private String school;

    @Column(name = "major", nullable = true)
    @Comment("전공")
    private String major;

    @Column(name = "degree", nullable = true)
    @Comment("학위")
    private String degree;

    // 학력 정보 업데이트
    public void updateEducation(String school, String major, String degree) {
        this.school = school;
        this.major = major;
        this.degree = degree;
    }
}
