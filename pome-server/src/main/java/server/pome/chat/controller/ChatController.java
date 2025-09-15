package server.pome.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.dto.request.ReadRequest;
import server.pome.chat.dto.response.CreateChatResponse;
import server.pome.chat.dto.response.GetChatListResponse;
import server.pome.chat.service.ChatMessageService;
import server.pome.global.domain.BaseEntity;
import server.pome.global.domain.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

  private final ChatMessageService chatMessageService;

  @Operation(summary = "채팅 생성")
  @Parameters({
      @Parameter(name = "mateId", description = "조회 당한 메이트의 ID", required = true),
      @Parameter(name = "userId", description = "조회한 회원 ID", required = true)
  })
  @PostMapping("/{mateId}/{userId}")
  public ResponseEntity<BaseResponse<CreateChatResponse>> createChat(
      @PathVariable Long mateId,
      @PathVariable Long userId,
      @Valid @RequestBody CreateChatRequest createChatRequest
  ) {
    CreateChatResponse result = chatMessageService.createChat(mateId, userId, createChatRequest);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  @Operation(summary = "필드의 전체 채팅 조회")
  @Parameters({
      @Parameter(name = "mateId", description = "조회 당한 메이트의 ID", required = true),
      @Parameter(name = "userId", description = "조회한 회원 ID", required = true)
  })
  @PostMapping("/list/{mateId}/{userId}")
  public ResponseEntity<BaseResponse<List<GetChatListResponse>>> getChatList(
      @PathVariable Long mateId,
      @PathVariable Long userId,
      @Valid @RequestBody ReadRequest readRequest,
      @ParameterObject
      @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    List<GetChatListResponse> result = chatMessageService.getChatList(mateId, userId, readRequest,
        pageable);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  @Operation(summary = "채팅 삭제")
  @Parameters({
      @Parameter(name = "mateId", description = "조회 당한 메이트의 ID", required = true),
      @Parameter(name = "userId", description = "조회한 회원 ID", required = true),
      @Parameter(name = "messageId", description = "삭제할 메시지 ID", required = true)
  })
  @PostMapping("/delete/{mateId}/{messageId}/{userId}")
  public ResponseEntity<BaseResponse<String>> deleteChat(
      @PathVariable Long mateId,
      @PathVariable Long messageId,
      @PathVariable Long userId,
      @Valid @RequestBody ReadRequest readRequest
  ) {
    chatMessageService.deleteChat(mateId, messageId, userId, readRequest);
    return ResponseEntity.ok(BaseResponse.success(messageId + ": 삭제 완료"));
  }
}
