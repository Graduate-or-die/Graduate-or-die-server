package server.pome.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateMessageRequest {

  @Schema(description = "채팅 내용", example = "안녕")
  private String content;

}
