package server.pome.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.dto.response.CreateChatResponse;
import server.pome.chat.service.ChatFieldReadService;
import server.pome.global.domain.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fields")
public class ChatFieldReadController {

  private final ChatFieldReadService chatFieldReadService;

  @Operation(summary = "최신 메시지까지 읽음 처리 (필드 화면 나갈 때 호출)")
  @Parameters({
      @Parameter(name = "fieldId", description = "채팅방 ID", required = true),
      @Parameter(name = "userId", description = "회원 ID", required = true)
  })
  @PostMapping("/read/{fieldId}/{userId}")
  public ResponseEntity<Void> readUpToLatest(
      @PathVariable Long fieldId,
      @PathVariable Long userId,
      @RequestBody CreateChatRequest request
  ) {
    chatFieldReadService.markReadUpToLatest(fieldId, userId, request);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "특정 메시지까지 읽음 처리")
  @Parameters({
      @Parameter(name = "mateId", description = "포트폴리오 소유자 ID", required = true),
      @Parameter(name = "messageId", description = "읽은 메시지 ID", required = true),
      @Parameter(name = "userId", description = "회원 ID", required = true)
  })
  @PostMapping("/read/{mateId}/{messageId}/{userId}")
  public ResponseEntity<Void> readUpTo(
      @PathVariable Long mateId,
      @PathVariable Long messageId,
      @PathVariable Long userId,
      @RequestBody CreateChatRequest request
  ) {
    chatFieldReadService.markReadUpTo(mateId, userId, messageId, request);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "필드별 읽지 않은 메시지 개수 조회")
  @Parameters({
      @Parameter(name = "mateId", description = "포트폴리오 소유자 ID", required = true),
      @Parameter(name = "userId", description = "회원 ID", required = true)
  })
  @GetMapping("/unread/{mateId}/{userId}")
  public ResponseEntity<Map<String, Long>> unreadCount(
      @PathVariable Long mateId,
      @PathVariable Long userId,
      @RequestBody CreateChatRequest request
  ) {
    long count = chatFieldReadService.countUnread(mateId, userId, request);
    return ResponseEntity.ok(Map.of("unread", count));
  }


}
