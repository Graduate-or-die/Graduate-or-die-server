package server.pome.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import server.pome.global.enums.TypeEnum;

@Getter
@Builder
public class UnreadResponse {

  @Schema(description = "항목명", example = "ACTIVITY")
  private TypeEnum portfolioType;

  @Schema(description = "블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "필드명", example = "역할")
  private String fieldKey;

  @Schema(description = "미읽음 여부")
  private Boolean hasUnread;

  public static UnreadResponse from(TypeEnum portfolioType, Long blockId, String fieldKey,
      Boolean hasUnread) {
    return UnreadResponse.builder()
        .portfolioType(portfolioType)
        .blockId(blockId)
        .fieldKey(fieldKey)
        .hasUnread(hasUnread)
        .build();
  }
}
