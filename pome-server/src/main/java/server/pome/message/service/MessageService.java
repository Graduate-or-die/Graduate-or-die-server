package server.pome.message.service;

import static server.pome.global.exception.BaseResponseStatus.INVALID_REQUEST_FORM;
import static server.pome.global.exception.BaseResponseStatus.NOT_MATCHED_MATE;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_PARTICIPANT;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.Message;
import server.pome.global.domain.MessageRoom;
import server.pome.global.domain.User;
import server.pome.global.enums.MateRequestStatus;
import server.pome.global.exception.BaseException;
import server.pome.mate.repository.MateRepository;
import server.pome.message.dto.request.CreateMessageRequest;
import server.pome.message.dto.response.CreateMessageResponse;
import server.pome.message.repository.MessageRepository;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final UserRepository userRepository;
  private final MessageRoomService messageRoomService;
  private final MessageRepository messageRepository;
  private final MateRepository mateRepository;

  // 메시지 생성 (전송)
  @Transactional
  public CreateMessageResponse createMessage(Long userId,
      CreateMessageRequest createMessageRequest) {

    if (createMessageRequest == null) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }

    // 유저 조회
    User sender = userRepository.findById(userId)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

    //채팅 내용 검증
    String content = createMessageRequest.getContent();
    if (content == null || content.isBlank()) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }
    content = content.strip();

    // 채팅 발신자의 메이트 조회
    Long mateId = mateRepository.findMateIdByUserIdAndStatus(userId, MateRequestStatus.ACCEPTED)
        .orElseThrow(() -> new BaseException(NOT_MATCHED_MATE));

    // 채팅방 조회
    MessageRoom messageRoom = messageRoomService.getOrCreateMessageRoom(userId, mateId);

    // 채팅방 참여자 검증
    if (!messageRoom.isParticipant(userId) || !messageRoom.isParticipant(mateId)) {
      throw new BaseException(USER_NOT_PARTICIPANT);
    }

    Message message = new Message(messageRoom, sender, content);
    messageRepository.save(message);

    return CreateMessageResponse.from(message.getId(), message.getContent());
  }
}
