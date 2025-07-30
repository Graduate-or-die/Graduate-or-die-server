package server.pome.portfolio.controller;

import lombok.RequiredArgsConstructor;
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

    // 항목별 공개범위 설정
    @PostMapping("/visibility/{userId}")
    public BaseResponse<VisibilityResponse> toggleVisibility(@PathVariable Long userId,
                                                             @RequestParam("typeId") Long typeId) {
        boolean updatedState = portfolioService.toggleVisible(userId, typeId);
        return BaseResponse.success(new VisibilityResponse(typeId, updatedState));
    }

    // 공개범위 여부 리스트 조회
    @GetMapping("/visibility/{userId}")
    public BaseResponse<List<VisibilityResponse>> getVisibilityList(@PathVariable Long userId) {
        List<VisibilityResponse> visibilityList = portfolioService.getVisibilityList(userId);
        return BaseResponse.success(visibilityList);
    }

}
