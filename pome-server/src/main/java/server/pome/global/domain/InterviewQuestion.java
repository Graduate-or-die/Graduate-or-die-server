package server.pome.global.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT", length = 1000)
    @Comment("질문")
    private String questionText;

    @Column(name = "topic", nullable = true)
    @Comment("대주제 분류")
    private String topic;

    @Column(name = "intent", nullable = true)
    @Comment("질문 의도")
    private String intent;

    @Column(name = "role", nullable = true)
    @Comment("특정 직군")
    private String role;

    @Column(name = "scope", nullable = true)
    @Comment("대상 범위")
    private String scope;

    @ElementCollection
    @CollectionTable(name = "question_keywords",
            joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "keywords", nullable = true)
    @Comment("핵심 태그/키워드")
    private List<String> keywords;

}
