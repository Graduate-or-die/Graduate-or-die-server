package server.pome.portfolio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.pome.global.domain.BaseResponse;
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
    public ResponseEntity<BaseResponse<VisibilityResponse>> toggleVisibility(@PathVariable Long userId,
                                                             @RequestParam("typeId") Long typeId) {
        boolean updatedState = portfolioService.toggleVisible(userId, typeId);
        return ResponseEntity.ok(BaseResponse.success(new VisibilityResponse(typeId, updatedState)));
    }

    @Operation(summary = "공개범위 여부 리스트 조회")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @GetMapping("/visibility/{userId}")
    public ResponseEntity<BaseResponse<List<VisibilityResponse>>>  getVisibilityList(@PathVariable Long userId) {
        List<VisibilityResponse> visibilityList = portfolioService.getVisibilityList(userId);
        return ResponseEntity.ok(BaseResponse.success(visibilityList));
    }

}
