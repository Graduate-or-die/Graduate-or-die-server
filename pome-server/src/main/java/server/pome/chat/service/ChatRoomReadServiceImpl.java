package server.pome.chat.service;

import static java.time.LocalDateTime.*;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.repository.ChatMessageRepository;
import server.pome.chat.repository.ChatRoomReadRepository;
import server.pome.chat.repository.ChatRoomRepository;
import server.pome.global.domain.ChatMessage;
import server.pome.global.domain.ChatRoom;
import server.pome.global.domain.ChatRoomRead;
import server.pome.global.domain.User;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatRoomReadServiceImpl implements ChatRoomReadService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomReadRepository chatRoomReadRepository;
  private final UserRepository userRepository;
  private final ChatRoomRepository chatRoomRepository;

  @Transactional
  @Override
  // 방 진입 및 스크롤 완료 시 최신 메시지까지 '읽음' 처리
  public void markReadUpToLatest(Long roomId, Long userId) {
    // 최신 메시지 id 조회 (메시지 없으면 0 반환)
    Long latestId = chatMessageRepository.findTopByRoom_IdOrderByIdDesc(roomId)
        .map(ChatMessage::getId).orElse(0L);

    markReadUpTo(roomId, userId, latestId);
  }

  @Transactional
  @Override
  // 마지막 가시 메시지 ID까지 '읽음' 처리
  public void markReadUpTo(Long roomId, Long userId, Long messageId) {
    // row 미존재 시 생성
    ensureRow(roomId, userId);

    // 메시지가 없으면 종료
    if (messageId == null || messageId <= 0L) {
      return;
    }

    int updated = chatRoomReadRepository.advancePointer(roomId, userId, messageId, now());

    // FK로 UPDATE 실패 시 엔티티로 보정
    if (updated == 0) {
      ChatRoomRead row = chatRoomReadRepository.findByRoomIdAndUserId(roomId, userId)
          .orElseThrow();
      row.advanceTo(messageId, now());
      chatRoomReadRepository.save(row);
    }
  }

  @Transactional(readOnly = true)
  @Override
  public long countUnread(Long roomId, Long userId) {
    long lastId = chatRoomReadRepository.findByRoomIdAndUserId(roomId, userId)
        .map(ChatRoomRead::getLastReadMessageId)
        .orElse(0L);

    return chatMessageRepository.countUnread(roomId, lastId, userId);
  }

  // 채팅방에 row 보장 (없으면 생성)
  private void ensureRow(Long roomId, Long userId) {
    chatRoomReadRepository.findByRoomIdAndUserId(roomId, userId).orElseGet(()->{
      // FK만 참조 (쓰기 전용 참조)
      ChatRoom roomRef = chatRoomRepository.getReferenceById(roomId);
      User userRef = userRepository.getReferenceById(userId);
      return chatRoomReadRepository.save(new ChatRoomRead(roomRef, userRef));
    });
  }
}
