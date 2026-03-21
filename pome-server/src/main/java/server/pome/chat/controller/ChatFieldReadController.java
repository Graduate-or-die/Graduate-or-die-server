package server.pome.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import server.pome.global.domain.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fields")
@Tag(name = "ChatRead", description = "채팅 읽음 API")
public class ChatFieldReadController {

  private final ChatFieldReadService chatFieldReadService;

  @Operation(summary = "최신 메시지까지 읽음 처리 (필드 화면 나갈 때 호출)")
  @Parameter(name = "portfolioOwnerId", description = "포트폴리오 소유자 ID", required = true)
  @PostMapping("/read/{portfolioOwnerId}")
  public ResponseEntity<Void> readUpToLatest(
      @PathVariable Long portfolioOwnerId,
      Authentication authentication,
      @RequestBody ReadRequest request
  ) {
    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    chatFieldReadService.markReadUpToLatest(portfolioOwnerId, userId, request);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "특정 메시지까지 읽음 처리")
  @Parameters({
      @Parameter(name = "portfolioOwnerId", description = "포트폴리오 소유자 ID", required = true),
      @Parameter(name = "messageId", description = "읽을 메시지 ID", required = true),
  })
  @PostMapping("/read/{portfolioOwnerId}/{messageId}")
  public ResponseEntity<Void> readUpTo(
      @PathVariable Long portfolioOwnerId,
      @PathVariable Long messageId,
      Authentication authentication,
      @RequestBody ReadRequest request
  ) {
    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    chatFieldReadService.markReadUpTo(portfolioOwnerId, userId, messageId, request);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "필드별 미읽음 메시지 존재 여부 조회")
  @Parameter(name = "portfolioOwnerId", description = "포트폴리오 소유자 ID", required = true)
  @PostMapping("/unread/{portfolioOwnerId}")
  public ResponseEntity<BaseResponse<List<UnreadResponse>>> getUnreadList(
      @PathVariable Long portfolioOwnerId,
      Authentication authentication,
      @RequestBody UnreadRequest request
  ) {
    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    List<UnreadResponse> result = chatFieldReadService.GetUnreadList(portfolioOwnerId, userId,
        request);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
