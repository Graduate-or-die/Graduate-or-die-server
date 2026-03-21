package server.pome.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UnreadRequest {

  @Schema(description = "타입 ID", example = "3")
  private Long typeId;
}
