package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(
        name = "interview_questions_history",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "question_text"})
        }
)
public class InterviewQuestionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("사용자 ID")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("질문")
    private String questionText;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdAt;
}
