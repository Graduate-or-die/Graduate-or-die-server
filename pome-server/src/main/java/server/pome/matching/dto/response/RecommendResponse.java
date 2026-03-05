package server.pome.matching.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecommendResponse {
    @Schema(description = "회원 ID", example = "1L")
    private Long userId;

  @Schema(description = "추천 메이트 목록")
  List<RecommendCandidateDTO> mates;

  public static RecommendResponse from(Long userId, List<RecommendCandidateDTO> dtoList) {
    return RecommendResponse.builder()
        .userId(userId)
        .mates(dtoList)
        .build();
  }
}
