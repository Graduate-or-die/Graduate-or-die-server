package server.pome.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.MessageRoom;
import server.pome.message.repository.MessageRoomRepository;

@Service
@RequiredArgsConstructor
public class MessageRoomService {

  private final MessageRoomRepository messageRoomRepository;
  
  // 채팅 생성 및 조회
  @Transactional
  public MessageRoom getOrCreateMessageRoom(Long user1Id, Long user2Id) {
    Long low = Math.min(user1Id, user2Id);
    Long high = Math.max(user1Id, user2Id);

    return messageRoomRepository.findByUserLowIdAndUserHighId(low, high)
        .orElseGet(() -> {
          // 채팅방이 존재하지 않으면 생성
          return messageRoomRepository.saveAndFlush(MessageRoom.of(user1Id, user2Id));
        });
  }
}
