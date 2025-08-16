package server.pome.award.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody; 
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.award.dto.request.SaveAwardRequest;
import server.pome.award.dto.response.SaveAwardResponse;
import server.pome.award.service.AwardService;
import server.pome.global.domain.BaseResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/awards")
@Tag(name = "Award", description = "포트폴리오_수상경력 API")
public class AwardController {

    private final AwardService awardService;

    @Operation(summary = "수상경력 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveAwardResponse>> saveAward(@PathVariable Long userId, @Valid @RequestBody SaveAwardRequest request) {

        SaveAwardResponse result = awardService.saveAward(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
