package server.pome.chat.service;

import java.util.ArrayList;
import java.util.List;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.dto.request.UnreadRequest;
import server.pome.chat.dto.response.UnreadResponse;

public interface ChatFieldReadService {

  // 최신까지 읽음
  void markReadUpToLatest(Long mateId, Long userId, CreateChatRequest request);

  // 특정 메시지까지 읽음
  void markReadUpTo(Long mateId, Long userId, Long messageId, CreateChatRequest request);

  // 필드별 미읽음 여부
  List<UnreadResponse> GetUnreadList(Long mateId, Long userId, UnreadRequest request);

}
