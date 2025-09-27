package server.pome.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetMessageListResponse {

  @Schema(description = "메시지 ID", example = "1")
  private Long messageId;

  @Schema(description = "발신자 ID", example = "1")
  private Long senderId;

  @Schema(description = "메시지 내용", example = "안녕")
  private String content;

  public static GetMessageListResponse from(Long messageId, Long senderId, String content
  ) {
    return GetMessageListResponse.builder()
        .messageId(messageId)
        .senderId(senderId)
        .content(content)
        .build();
  }
}
