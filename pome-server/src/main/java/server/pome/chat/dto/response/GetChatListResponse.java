package server.pome.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetChatListResponse {

  @Schema(description = "메시지 ID", example = "1")
  private Long messageId;

  @Schema(description = "발신자 ID", example = "1")
  private Long senderId;

  @Schema(description = "채팅 내용", example = "1")
  private String content;

  public static GetChatListResponse from(Long messageId, Long senderId, String content) {
    return GetChatListResponse.builder()
        .messageId(messageId)
        .senderId(senderId)
        .content(content)
        .build();
  }
}
