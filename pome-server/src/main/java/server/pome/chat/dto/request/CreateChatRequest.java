package server.pome.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateChatRequest {

  @NotNull
  @Schema(description = "타입 ID", example = "3")
  private Long typeId;

  @NotNull
  @Schema(description = "블록 ID", example = "1")
  private Long blockId;

  @NotBlank
  @Schema(description = "필드명", example = "activityRole")
  private String fieldKey;

  @NotBlank
  @Schema(description = "채팅 내용", example = "이 활동에서 맡은 역할이 무엇인가요?")
  private String content;
}
