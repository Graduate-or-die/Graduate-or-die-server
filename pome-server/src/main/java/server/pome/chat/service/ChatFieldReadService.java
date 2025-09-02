package server.pome.chat.service;

import server.pome.chat.dto.request.CreateChatRequest;

public interface ChatFieldReadService {

  // 최신까지 읽음
  void markReadUpToLatest(Long mateId, Long userId, CreateChatRequest request);

  // 특정 메시지까지 읽음
  void markReadUpTo(Long mateId, Long userId, Long messageId, CreateChatRequest request);

  // 필드별 미읽음 개수 카운트
  long countUnread(Long mateId, Long userId, CreateChatRequest request);

}
