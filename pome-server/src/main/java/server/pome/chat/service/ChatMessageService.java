package server.pome.chat.service;

import static server.pome.global.exception.BaseResponseStatus.CHAT_FIELD_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.INVALID_CHAT_FORM;
import static server.pome.global.exception.BaseResponseStatus.INVALID_TYPE_ENUM;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_PARTICIPANT;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.dto.response.CreateChatResponse;
import server.pome.chat.repository.ChatFieldRepository;
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
  private final ChatFieldRepository chatFieldRepository;
  private final UserRepository userRepository;
  private final ChatFieldReadService chatFieldReadService;

  private final ChatFieldService chatFieldService;
  private final MateService mateService;

  // 채팅 생성 (전송)
  @Transactional
  public CreateChatResponse createChat(Long mateId, Long senderId,
      CreateChatRequest createChatRequest) {

    // 채팅방 조회
    ChatField field = chatFieldService.getOrCreate(
        mateId, createChatRequest.getPortfolioType(),
        createChatRequest.getBlockId(),
        createChatRequest.getFieldKey());

    Long fieldId = field.getId();

    // 참가한 유저인지 검증 및 조회
    if (!isParticipants(field, senderId)) {
      throw new BaseException(USER_NOT_PARTICIPANT);
    }

    // 유저 조회
    User sender = userRepository.findById(senderId)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

    // 포트폴리오 TYPEENUM 검증
    if (createChatRequest.getPortfolioType() == null) {
      throw new BaseException(INVALID_TYPE_ENUM);
    }

    // 채팅 내용 검증
    String content = createChatRequest.getContent();
    if (content == null || content.isBlank()) {
      throw new BaseException(INVALID_CHAT_FORM);
    }
    content = content.strip();

    // 메시지 생성, 저장
    ChatMessage message = new ChatMessage(field, sender, content);
    chatMessageRepository.save(message);

    // 발송자의 읽음 포인터를 새 메시지까지 전진
    chatFieldReadService.markReadUpTo(mateId, senderId, message.getId(), createChatRequest);

    return CreateChatResponse.from(message.getId(), field.getFieldKey(), senderId,
        createChatRequest.getContent());
  }

  /** 헬퍼 메서드 */
  // 조회한 채팅방에 권한이 있는 유저인지 확인(메이트 또는 유저 검증)
  private boolean isParticipants(ChatField field, Long userId) {
    // 포트폴리오 소유자라면 채팅방 권한 있음
    Long ownerId = field.getOwner().getId();
    if (Objects.equals(ownerId, userId)) {
      return true;
    }
    Long mateId = mateService.getMateId(ownerId);
    return mateService.isAcceptedMates(mateId, ownerId);
  }

}
