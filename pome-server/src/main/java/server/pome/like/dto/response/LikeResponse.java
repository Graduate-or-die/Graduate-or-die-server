package server.pome.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {

  @Schema(description = "유저 ID", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long userId;

  @Schema(description = "유저의 좋아요 수", example = "1", requiredMode = RequiredMode.REQUIRED)
  private int likeCount;

  public static LikeResponse toEntity(Long userId, int likeCount) {
    return LikeResponse.builder()
        .userId(userId)
        .likeCount(likeCount)
        .build();
  }

}
