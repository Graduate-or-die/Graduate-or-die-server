package server.pome.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.dto.response.CreateChatResponse;
import server.pome.chat.service.ChatMessageService;
import server.pome.global.domain.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

  private final ChatMessageService chatMessageService;

  @Operation(summary = "채팅 생성")
  @Parameters({
      @Parameter(name = "roomId", description = "채팅방 ID", required = true),
      @Parameter(name = "userId", description = "회원 ID", required = true)
  })
  @PostMapping("/{roomId}/{userId}")
  public ResponseEntity<BaseResponse<CreateChatResponse>> createChat(
      @PathVariable Long roomId,
      @PathVariable Long userId,
      @Valid @RequestBody CreateChatRequest createChatRequest
  ) {
    CreateChatResponse result = chatMessageService.createChat(roomId, userId, createChatRequest);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
