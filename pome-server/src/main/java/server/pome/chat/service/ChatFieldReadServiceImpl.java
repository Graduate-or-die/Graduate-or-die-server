package server.pome.chat.service;

import static java.time.LocalDateTime.now;
import static server.pome.global.exception.BaseResponseStatus.CHAT_READ_ERROR;
import static server.pome.global.exception.BaseResponseStatus.INVALID_CHAT_FORM;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.repository.ChatFieldReadRepository;
import server.pome.chat.repository.ChatFieldRepository;
import server.pome.chat.repository.ChatMessageRepository;
import server.pome.global.domain.ChatField;
import server.pome.global.domain.ChatFieldRead;
import server.pome.global.domain.ChatMessage;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatFieldReadServiceImpl implements ChatFieldReadService {

  private final ChatFieldReadRepository chatFieldReadRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatFieldRepository chatFieldRepository;
  private final UserRepository userRepository;
  private final ChatFieldService chatFieldService;

  // 최신까지 읽음
  @Transactional
  @Override
  public void markReadUpToLatest(Long mateId, Long userId, CreateChatRequest request) {
    Long fieldId = getFieldId(mateId, request);

    Long latestId = chatMessageRepository.findTopByField_IdOrderByIdDesc(fieldId)
        .map(ChatMessage::getId).orElse(0L);
    markReadUpTo(fieldId, userId, latestId, request);

    if (latestId <= 0L) {
      ensureRow(fieldId, userId);
      return;
    }

    markReadUpToFieldId(fieldId, userId, latestId);
  }

  // 특정 메시지까지 읽음
  @Transactional
  @Override
  public void markReadUpTo(Long mateId, Long userId, Long messageId, CreateChatRequest request) {
    Long fieldId = getFieldId(mateId, request);

    if (messageId == null || messageId <= 0L) {
      ensureRow(fieldId, userId);
      throw new BaseException(INVALID_CHAT_FORM);
    }

    markReadUpToFieldId(fieldId, userId, messageId);
  }

  // 필드별 미읽음 개수
  @Transactional(readOnly = true)
  @Override
  public long countUnread(Long mateId, Long userId, CreateChatRequest request) {
    Long fieldId = getFieldId(mateId, request);
    long last = chatFieldReadRepository.findByField_IdAndUser_Id(fieldId, userId)
        .map(ChatFieldRead::getLastReadMessageId).orElse(0L);

    return chatMessageRepository.countUnread(fieldId, last, userId);
  }

  @Transactional
  protected void markReadUpToFieldId(Long fieldId, Long userId, Long messageId) {
    ensureRow(fieldId, userId);

    int updated = chatFieldReadRepository.advancePointer(fieldId, userId, messageId, now());
    if (updated == 0) {
      ChatFieldRead row = chatFieldReadRepository.findByField_IdAndUser_Id(fieldId, userId)
          .orElseThrow(() -> new BaseException(CHAT_READ_ERROR));
      row.advanceTo(messageId, now());
      chatFieldReadRepository.save(row);
    }
  }

  // row가 존재하는지 보장
  private void ensureRow(Long fieldId, Long userId) {
    chatFieldReadRepository.findByField_IdAndUser_Id(fieldId, userId)
        .orElseGet(() -> {
          ChatField fieldRef = chatFieldRepository.getReferenceById(fieldId);
          User userRef = userRepository.getReferenceById(userId);
          try {
            // 즉시 flush하여 제약 위반을 여기서 감지
            return chatFieldReadRepository.saveAndFlush(new ChatFieldRead(fieldRef, userRef));
          } catch (DataIntegrityViolationException e) {
            // 다른 트랜잭션이 먼저 넣었음 → 새 트랜잭션에서 재조회
            return reloadRow(fieldId, userId);
          }
        });
  }

  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  protected ChatFieldRead reloadRow(Long fieldId, Long userId) {
    return chatFieldReadRepository.findByField_IdAndUser_Id(fieldId, userId)
        .orElseThrow(() -> new BaseException(CHAT_READ_ERROR));
  }

  private Long getFieldId(Long userId, CreateChatRequest request) {
    return chatFieldService.getOrCreate(
        userId, request.getPortfolioType(),
        request.getBlockId(),
        request.getFieldKey()).getId();
  }
}
