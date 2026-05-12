package server.pome.interviewQuestion.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.interviewQuestion.service.InterviewEmbeddingService;
import server.pome.interviewQuestion.service.InterviewQuestionBulkService;
import server.pome.interviewQuestion.service.InterviewQuestionGenerator;
import server.pome.interviewQuestion.service.InterviewSearchService;
import server.pome.mate.service.MateQueryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/interview-questions")
public class InterviewQuestionAdminController {

    private final InterviewQuestionBulkService bulkService;
    private final InterviewEmbeddingService interviewEmbeddingService;
    private final InterviewSearchService interviewSearchService;
    private final InterviewQuestionGenerator interviewQuestiongenerator;
    private final MateQueryService mateQueryService;



    @Operation(summary = "면접 질문 CSV Bulk Insert")
    @PostMapping("/bulk")
    public ResponseEntity<BaseResponse<String>> bulkInsert() {

        bulkService.bulkInsert();

        return ResponseEntity.ok(
                BaseResponse.success("면접 질문 Bulk Insert 완료")
        );
    }

    @Operation(summary = "면접 질문 임베딩 생성 및 Qdrant 업로드")
    @PostMapping("/embedding")
    public ResponseEntity<BaseResponse<String>> generateEmbedding() {

        interviewEmbeddingService.generateAndUpload();

        return ResponseEntity.ok(BaseResponse.success("면접 질문 임베딩 업로드"));

    }

    @Operation(summary = "AI 면접 질문 생성")
    @PostMapping("/generate")
    public List<String> generateInterview(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        String portfolio =
                interviewSearchService.buildPortfolio(userId);

        List<String> similarQuestions =
                interviewSearchService.findSimilarQuestions(portfolio);

        return interviewQuestiongenerator.generateQuestion(
                user,
                portfolio,
                similarQuestions
        );
    }
}
