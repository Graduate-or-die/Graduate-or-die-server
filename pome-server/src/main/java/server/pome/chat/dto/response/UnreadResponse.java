package server.pome.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnreadResponse {

  @Schema(description = "타입 ID", example = "3")
  private Long typeId;

  @Schema(description = "블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "필드명", example = "activityRole")
  private String fieldKey;

  @Schema(description = "미읽음 여부")
  private Boolean hasUnread;

  public static UnreadResponse from(Long typeId, Long blockId, String fieldKey,
      Boolean hasUnread) {
    return UnreadResponse.builder()
        .typeId(typeId)
        .blockId(blockId)
        .fieldKey(fieldKey)
        .hasUnread(hasUnread)
        .build();
  }
}
