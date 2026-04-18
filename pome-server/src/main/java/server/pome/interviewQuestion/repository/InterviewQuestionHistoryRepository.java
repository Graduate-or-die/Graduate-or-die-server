package server.pome.interviewQuestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.pome.global.domain.InterviewQuestionHistory;

import java.util.List;

public interface InterviewQuestionHistoryRepository extends JpaRepository<InterviewQuestionHistory, Long> {

    List<InterviewQuestionHistory> findTop20ByUser_IdOrderByCreatedAtDesc(Long userId);

}
