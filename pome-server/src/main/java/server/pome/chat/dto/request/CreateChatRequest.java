package server.pome.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import server.pome.portfolio.type.TypeEnum;

@Getter
public class CreateChatRequest {

  @Schema(description = "항목명", example = "ACTIVITY")
  private TypeEnum portfolioType;

  @Schema(description = "블록 ID", example = "1")
  private Long blockId;

  @Schema(description = "필드명", example = "역할")
  private String fieldKey;

  @Schema(description = "채팅 내용", example = "무슨 역할인가요")
  private String content;

}
