package server.pome.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateChatRequest {

  @Schema(description = "타입 ID", example = "3")
  private Long typeId;

  @Schema(description = "블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "필드명", example = "activityRole")
  private String fieldKey;

  @Schema(description = "채팅 내용", example = "이 활동에서 맡은 역할이 무엇인가요?")
  private String content;
}
