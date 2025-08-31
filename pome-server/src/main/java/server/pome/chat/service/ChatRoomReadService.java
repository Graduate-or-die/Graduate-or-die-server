package server.pome.chat.service;

public interface ChatRoomReadService {

  // 읽기 포인터를 최신으로 갱신
  void markReadUpToLatest(Long roomId, Long userId);

  // 마지막으로 읽은 메시지 id까지 '읽음' 처리
  void markReadUpTo(Long roomId, Long userId, Long messageId);

  // 미읽음 개수 반환 (NPE 방지용으로 long타입 선언)
  long countUnread(Long roomId, Long userId);

}
