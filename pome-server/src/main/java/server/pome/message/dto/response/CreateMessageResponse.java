package server.pome.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateMessageResponse {

  @Schema(description = "메시지 ID", example = "1")
  private Long messageId;

  @Schema(description = "채팅 내용", example = "안녕")
  private String content;

  public static CreateMessageResponse from(Long messageId, String content) {
    return CreateMessageResponse.builder()
        .messageId(messageId)
        .content(content)
        .build();
  }
}
