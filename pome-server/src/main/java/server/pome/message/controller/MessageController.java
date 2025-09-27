package server.pome.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.message.dto.request.CreateMessageRequest;
import server.pome.message.dto.response.CreateMessageResponse;
import server.pome.message.service.MessageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "채팅 생성(전송)")
  @Parameters({
      @Parameter(name = "userId", description = "메시지를 생성한 유저ID")
  })
  @PostMapping("/{userId}")
  public ResponseEntity<BaseResponse<CreateMessageResponse>> createMessage(
      @PathVariable Long userId,
      @Valid @RequestBody CreateMessageRequest createMessageRequest) {
    CreateMessageResponse result = messageService.createMessage(userId, createMessageRequest);
    return ResponseEntity.ok(BaseResponse.success(result));
  }


}
