package server.pome.interviewQuestion.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.interviewQuestion.service.InterviewQuestionBulkService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/interview-questions")
public class InterviewQuestionAdminController {
    private final InterviewQuestionBulkService bulkService;

    @Operation(summary = "면접 질문 CSV Bulk Insert")
    @PostMapping("/bulk")
    public ResponseEntity<BaseResponse<String>> bulkInsert() {

        bulkService.bulkInsert();

        return ResponseEntity.ok(
                BaseResponse.success("면접 질문 Bulk Insert 완료")
        );
    }
}
