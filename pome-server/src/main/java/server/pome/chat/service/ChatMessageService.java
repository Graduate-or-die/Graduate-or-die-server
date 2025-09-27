package server.pome.chat.service;

import static server.pome.global.exception.BaseResponseStatus.CHAT_DELETE_DISABLED;
import static server.pome.global.exception.BaseResponseStatus.CHAT_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.INVALID_REQUEST_FORM;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_PARTICIPANT;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.dto.request.ReadRequest;
import server.pome.chat.dto.response.CreateChatResponse;
import server.pome.chat.dto.response.GetChatListResponse;
import server.pome.chat.repository.ChatMessageRepository;
import server.pome.global.domain.ChatField;
import server.pome.global.domain.ChatMessage;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.mate.service.MateService;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;
  private final ChatFieldReadService chatFieldReadService;
  private final ChatFieldService chatFieldService;
  private final MateService mateService;

  // 채팅 생성 (전송)
  @Transactional
  public CreateChatResponse createChat(Long mateId, Long senderId,
      CreateChatRequest createChatRequest) {

    if (createChatRequest == null) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }

    // 유저 조회
    User sender = userRepository.findById(senderId)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

    // 채팅 내용 검증
    String content = createChatRequest.getContent();
    if (content == null || content.isBlank()) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }
    content = content.strip();

    // 채팅방 조회
    ChatField field = chatFieldService.getOrCreate(
        mateId, createChatRequest.getPortfolioType(),
        createChatRequest.getBlockId(),
        createChatRequest.getFieldKey());

    // 참가한 유저인지 검증 및 조회
    isParticipants(field, senderId);

    // 메시지 생성, 저장
    ChatMessage message = new ChatMessage(field, sender, content);
    chatMessageRepository.save(message);

    // 발송자의 읽음 포인터를 새 메시지까지 전진
    ReadRequest readRequest = new ReadRequest(createChatRequest.getPortfolioType(),
        createChatRequest.getBlockId(), createChatRequest.getFieldKey());
    chatFieldReadService.markReadUpTo(mateId, senderId, message.getId(), readRequest);

    return CreateChatResponse.from(message.getId(), field.getFieldKey(), senderId,
        content);
  }

  // 채팅 목록 조회
  @Transactional(readOnly = true)
  public List<GetChatListResponse> getChatList(Long mateId, Long userId,
      ReadRequest readRequest, Pageable pageable) {

    if (readRequest == null) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }

    // 유저 조회
    User sender = userRepository.findById(userId)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

    // 채팅방 조회
    ChatField field = chatFieldService.getOrCreate(mateId, readRequest.getPortfolioType(),
        readRequest.getBlockId(), readRequest.getFieldKey());

    // 참가한 유저인지 검증 및 조회
    isParticipants(field, userId);

    // 메시지 조회
    Page<ChatMessage> page = chatMessageRepository
        .findByFieldIdOrderByIdAsc(field.getId(), pageable);

    List<GetChatListResponse> responses = page.getContent().stream()
        .map(message -> GetChatListResponse.from(
            message.getId(),
            message.getSender().getId(),
            message.getContent()
        )).toList();

    return responses;
  }

  // 특정 채팅 삭제
  @Transactional
  public void deleteChat(Long mateId, Long messageId, Long userId, ReadRequest readRequest) {
    // 유저 조회
    User sender = userRepository.findById(userId)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

    // 채팅방 조회
    ChatField field = chatFieldService.getOrCreate(mateId, readRequest.getPortfolioType(),
        readRequest.getBlockId(), readRequest.getFieldKey());

    // 메시지 조회
    ChatMessage message = chatMessageRepository.findById(messageId)
        .orElseThrow(() -> new BaseException(CHAT_NOT_FOUND));

    // 본인의 메시지만 삭제 가능
    if (!message.getSender().getId().equals(userId)) {
      throw new BaseException(CHAT_DELETE_DISABLED);
    }

    // 조회한 필드 내 메시지가 맞는지 검증
    if (!message.getField().getId().equals(field.getId())) {
      throw new BaseException(CHAT_NOT_FOUND);
    }

    // 메시지 삭제
    chatMessageRepository.delete(message);

  }

  /** 헬퍼 메서드 */
  // 조회한 채팅방에 권한이 있는 유저인지 확인(메이트 또는 유저 검증)
  private void isParticipants(ChatField field, Long userId) {
    // 포트폴리오 소유자, 소유자의 메이트가 아니라면 권한 없음
    Long ownerId = field.getOwner().getId();
    if (!Objects.equals(ownerId, userId) && !(mateService.isAcceptedMates(userId, ownerId))) {
      throw new BaseException(USER_NOT_PARTICIPANT);
    }
  }


}
