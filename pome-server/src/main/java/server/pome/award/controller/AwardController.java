package server.pome.award.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import server.pome.award.dto.request.SaveAwardRequest;
import server.pome.award.dto.request.UpdateAwardRequest;
import server.pome.award.dto.response.SaveAwardResponse;
import server.pome.award.dto.response.UpdateAwardResponse;
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

    @Operation(summary = "수상경력 수정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @Parameter(name = "blockId", description = "수상경력 ID", required = true)
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<UpdateAwardResponse>> updateAward(@PathVariable Long userId, @RequestParam("blockId") Long blockId, @Valid @RequestBody UpdateAwardRequest request) {
        UpdateAwardResponse result = awardService.updateAward(userId, blockId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
