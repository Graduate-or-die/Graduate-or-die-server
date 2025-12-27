package server.pome.portfolio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.pome.global.domain.BaseResponse;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.dto.response.GetPortfolioResponse;
import server.pome.portfolio.dto.response.PreviewResponse;
import server.pome.portfolio.dto.response.VisibilityResponse;
import server.pome.portfolio.service.PortfolioService;

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
        @RequestParam(required = false, name = "type") TypeEnum type
    ) {
        Object result = portfolioService.getPortfolioSection(userId, type);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
