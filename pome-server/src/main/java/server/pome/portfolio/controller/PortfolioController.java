package server.pome.portfolio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.portfolio.dto.request.BulkDeletePortfolioBlockRequest;
import server.pome.portfolio.dto.response.PreviewResponse;
import server.pome.portfolio.dto.response.VisibilityResponse;
import server.pome.portfolio.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "항목별 공개범위 설정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @Parameter(name = "typeId", description = "항목 ID", required = true)
    @PostMapping("/visibility/{userId}")
    public ResponseEntity<BaseResponse<VisibilityResponse>> toggleVisibility(
        @PathVariable Long userId,
        @RequestParam("typeId") Long typeId) {
        boolean updatedState = portfolioService.toggleVisible(userId, typeId);
        return ResponseEntity.ok(
            BaseResponse.success(new VisibilityResponse(typeId, updatedState)));
    }

    @Operation(summary = "공개범위 여부 리스트 조회")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @Parameter(name = "limit", description = "미리보기 개수 (최대 3개)", required = false)
    @GetMapping("/visibility/{userId}")
    public ResponseEntity<BaseResponse<PreviewResponse>> getVisibilityAndPreview(
        @PathVariable Long userId,
        @RequestParam(required = false) Integer limit,
        @RequestParam(required = false) List<Long> typeIds
    ) {
        PreviewResponse response = portfolioService.getVisibilityAndPreview(userId, limit, typeIds);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "포트폴리오 항목별 조회")
    @Parameters({
        @Parameter(name = "userId", description = "회원 ID", required = true),
        @Parameter(name = "typeId", description = "항목 ID")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<Object>> getPortfolio(
        @PathVariable Long userId,
        @RequestParam(required = false) Long typeId
    ) {
        Object result = portfolioService.getPortfolioSection(userId, typeId);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "포트폴리오 블록별 삭제")
    @Parameters({
            @Parameter(name = "typeId", description = "항목 ID", required = true),
            @Parameter(name = "blockId", description = "블록 ID", required = true)
    })
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deletePortfolioBlock(
            @AuthenticationPrincipal User user,
            @RequestParam Long typeId,
            @RequestParam Long blockId
    ) {
        portfolioService.deletePortfolioBlock(user.getId(), typeId, blockId);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @Operation(summary = "포트폴리오 벌크 삭제")
    @Parameters({
            @Parameter(name = "typeId", description = "항목 ID", required = true),
            @Parameter(name = "blockIds", description = "삭제할 블록 ID 리스트", required = true)
    })
    @PostMapping("/bulkDelete")
    public ResponseEntity<BaseResponse<Void>> bulkDeletePortfolioBlocks(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BulkDeletePortfolioBlockRequest request
    ) {
        portfolioService.deletePortfolioBlocks(
                user.getId(),
                request.getTypeId(),
                request.getBlockIds()
        );
        return ResponseEntity.ok(BaseResponse.success(null));
    }
}
