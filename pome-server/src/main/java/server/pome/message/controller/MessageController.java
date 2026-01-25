package server.pome.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.message.dto.request.CreateMessageRequest;
import server.pome.message.dto.response.CreateMessageResponse;
import server.pome.message.dto.response.GetMessageListResponse;
import server.pome.message.service.MessageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "채팅 생성(전송)")
  @PostMapping
  public ResponseEntity<BaseResponse<CreateMessageResponse>> createMessage(
          Authentication authentication,
      @Valid @RequestBody CreateMessageRequest createMessageRequest) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    CreateMessageResponse result = messageService.createMessage(userId, createMessageRequest);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  @Operation(summary = "채팅 목록 조회")
  @GetMapping
  public ResponseEntity<BaseResponse<List<GetMessageListResponse>>> getMessageList(
          Authentication authentication,
      @ParameterObject
      @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();
    List<GetMessageListResponse> result = messageService.getMessageList(userId, pageable);
    return ResponseEntity.ok(BaseResponse.success(result));
  }

  }
