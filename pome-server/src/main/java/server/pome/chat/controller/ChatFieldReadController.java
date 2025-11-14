package server.pome.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.chat.dto.request.ReadRequest;
import server.pome.chat.dto.request.UnreadRequest;
import server.pome.chat.dto.response.UnreadResponse;
import server.pome.chat.service.ChatFieldReadService;
import server.pome.global.domain.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fields")
@Tag(name = "ChatRead", description = "채팅 읽음용 API")
public class ChatFieldReadController {

  private final ChatFieldReadService chatFieldReadService;

  @Operation(summary = "최신 메시지까지 읽음 처리 (필드 화면 나갈 때 호출)")
  @Parameters({
      @Parameter(name = "mateId", description = "포트폴리오 소유자 ID", required = true),
      @Parameter(name = "userId", description = "회원 ID", required = true)
  })
  @PostMapping("/read/{mateId}/{userId}")
  public ResponseEntity<Void> readUpToLatest(
      @PathVariable Long mateId,
      @PathVariable Long userId,
      @RequestBody ReadRequest request
  ) {
    chatFieldReadService.markReadUpToLatest(mateId, userId, request);
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
      @RequestBody ReadRequest request
  ) {
    chatFieldReadService.markReadUpTo(mateId, userId, messageId, request);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "필드별 읽지 않은 메시지 존재 여부 조회")
  @Parameters({
      @Parameter(name = "mateId", description = "포트폴리오 소유자 ID", required = true),
      @Parameter(name = "userId", description = "회원 ID", required = true)
  })
  @PostMapping("/unread/{mateId}/{userId}")
  public ResponseEntity<BaseResponse<List<UnreadResponse>>> getUnreadList(
      @PathVariable Long mateId,
      @PathVariable Long userId,
      @RequestBody UnreadRequest request
  ) {
    List<UnreadResponse> result = chatFieldReadService.GetUnreadList(mateId, userId, request);
    return ResponseEntity.ok(BaseResponse.success(result));

  }


}
