package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "experiences")
public class Experience extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

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
    private LocalDateTime experienceStartAt;

    @Column(name = "experience_end_at", nullable = true)
    @Comment("근무 마감일")
    private LocalDateTime experienceEndAt;
}
