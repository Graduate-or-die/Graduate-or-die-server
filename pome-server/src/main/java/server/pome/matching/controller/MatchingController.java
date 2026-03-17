package server.pome.matching.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.matching.application.query.MatchingUserQueryService;
import server.pome.matching.dto.response.RecommendUserListResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching")
@Tag(name = "Matching", description = "메이트 추천 API")
public class MatchingController {

  private final MatchingUserQueryService matchingUserQueryService;

  @Operation(summary = "추천 메이트 10명 목록 조회")
  @GetMapping
  public ResponseEntity<BaseResponse<RecommendUserListResponse>> getRecommendUser(
      @AuthenticationPrincipal User user) {
    RecommendUserListResponse result = matchingUserQueryService.getRecommendUser(user.getId());
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
