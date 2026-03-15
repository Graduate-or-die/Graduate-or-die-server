package server.pome.matching.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RecommendUserListResponse(
    @Schema(description = "조회 기준 유저 ID", example = "1")
    Long userId,
    @Schema(description = "태그 유사도가 높은 유저 목록")
    List<RecommendUserResponse> users
) {

  public static RecommendUserListResponse from(Long userId, List<RecommendUserResponse> users) {
    return new RecommendUserListResponse(userId, users);
  }
}
