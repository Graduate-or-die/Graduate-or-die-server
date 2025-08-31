package server.pome.chat.service;

import static server.pome.global.exception.BaseResponseStatus.CHATROOM_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.INVALID_CHAT_FORM;
import static server.pome.global.exception.BaseResponseStatus.INVALID_TYPE_ENUM;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_PARTICIPANT;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.dto.response.CreateChatResponse;
import server.pome.chat.repository.ChatMessageRepository;
import server.pome.chat.repository.ChatRoomRepository;
import server.pome.global.domain.ChatMessage;
import server.pome.global.domain.ChatRoom;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;
  private final ChatRoomReadService chatRoomReadService;

  // 채팅 생성 (전송)
  @Transactional
  public CreateChatResponse createChat(Long roomId, Long senderId,
      CreateChatRequest createChatRequest) {
    // 채팅방 조회
    ChatRoom room = findRoomById(roomId);

    // 참가한 유저인지 검증 및 조회
    if (!room.isParticipant(senderId)) {
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
    if (createChatRequest.getContent() == null || createChatRequest.getContent().isBlank()) {
      throw new BaseException(INVALID_CHAT_FORM);
    }

    // 메시지 생성, 저장
    User senderRef = userRepository.getReferenceById(senderId);
    ChatMessage message = new ChatMessage(room, senderRef, createChatRequest.getPortfolioType(),
        createChatRequest.getBlockId(), createChatRequest.getField(),
        createChatRequest.getContent());
    chatMessageRepository.save(message);

    // 발송자의 읽음 포인터를 새 메시지까지 전진
    chatRoomReadService.markReadUpTo(roomId, senderId, message.getId());

    return CreateChatResponse.from(message.getId(), roomId, senderId,
        createChatRequest.getContent());
  }

  /** 헬퍼 메서드 */
  private ChatRoom findRoomById(Long roomId) {
    // 채팅방 조회
    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new BaseException(CHATROOM_NOT_FOUND));
    return room;
  }


}
