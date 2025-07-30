package server.pome.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.pome.global.domain.BaseResponse;
import server.pome.portfolio.dto.response.VisibilityResponse;
import server.pome.portfolio.service.PortfolioService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    // 항목별 공개범위 설정
    @PostMapping("/visibility/{userId}")
    public BaseResponse<VisibilityResponse> toggleVisibility(@PathVariable Long userId,
                                                             @RequestParam("typeId") Long typeId) {
        boolean updatedState = portfolioService.toggleVisible(userId, typeId);
        return BaseResponse.success(new VisibilityResponse(typeId, updatedState));
    }

}
