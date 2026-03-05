package server.pome.matching.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.matching.application.recommend.MateRecommendationService;
import server.pome.matching.dto.response.RecommendCandidateDTO;
import server.pome.matching.dto.response.RecommendResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching")
public class MatchingController {

  private final MateRecommendationService recommendationService;

  @GetMapping
  public ResponseEntity<BaseResponse<RecommendResponse>> recommend(
      @AuthenticationPrincipal User user) {
    Long userId = user.getId();

    var mates = recommendationService.recommend(userId);

    var dtoList = mates.stream()
        .map(m -> new RecommendCandidateDTO(m.userId(), m.score()))
        .toList();

    RecommendResponse result = RecommendResponse.from(userId, dtoList);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
