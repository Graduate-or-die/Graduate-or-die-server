package server.pome.interviewQuestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.pome.global.domain.InterviewQuestion;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
}
