package server.pome.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateChatResponse {

  @Schema(description = "채팅 ID", example = "1")
  private Long chatId;

  @Schema(description = "채팅방 ID", example = "1")
  private Long chatRoomId;

  @Schema(description = "발신자 ID", example = "1")
  private Long senderId;

  @Schema(description = "채팅 내용", example = "1")
  private String content;

  public static CreateChatResponse from(Long chatId, Long chatRoomId, Long senderId, String content) {
    return CreateChatResponse.builder()
        .chatId(chatId)
        .chatRoomId(chatRoomId)
        .senderId(senderId)
        .content(content)
        .build();
  }
}
