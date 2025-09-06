package server.pome.chat.service.impl;

import static java.time.LocalDateTime.now;
import static server.pome.global.exception.BaseResponseStatus.CHAT_READ_ERROR;
import static server.pome.global.exception.BaseResponseStatus.INVALID_CHAT_FORM;
import static server.pome.global.exception.BaseResponseStatus.INVALID_TYPE_ENUM;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.dto.request.CreateChatRequest;
import server.pome.chat.dto.request.UnreadRequest;
import server.pome.chat.dto.response.CreateChatResponse;
import server.pome.chat.dto.response.UnreadResponse;
import server.pome.chat.repository.ChatFieldReadRepository;
import server.pome.chat.repository.ChatFieldRepository;
import server.pome.chat.repository.ChatMessageRepository;
import server.pome.chat.service.ChatFieldReadService;
import server.pome.chat.service.ChatFieldService;
import server.pome.global.domain.ChatField;
import server.pome.global.domain.ChatFieldRead;
import server.pome.global.domain.ChatMessage;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.portfolio.type.TypeEnum;
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
    // request 유효성 검사
    validCreateChatRequest(request);

    Long fieldId = getFieldId(mateId, request);

    // 가장 최신 메시지 Id 조회 (없으면 0 반환)
    Long latestId = chatMessageRepository.findTopByField_IdOrderByIdDesc(fieldId)
        .map(ChatMessage::getId)
        .orElse(0L);

    // 유효성 검증
    if (latestId <= 0L) {
      getReadPointer(fieldId, userId);
      return;
    }

    // 가장 최신 메시지까지 읽음 표시
    markReadUpToFieldId(fieldId, userId, latestId);
  }

  // 특정 메시지까지 읽음
  @Transactional
  @Override
  public void markReadUpTo(Long mateId, Long userId, Long messageId, CreateChatRequest request) {
    // request 유효성 검사
    validCreateChatRequest(request);

    Long fieldId = getFieldId(mateId, request);

    if (messageId == null || messageId <= 0L) {
      getReadPointer(fieldId, userId);
      throw new BaseException(INVALID_CHAT_FORM);
    }

    markReadUpToFieldId(fieldId, userId, messageId);
  }

  // 필드별 미읽음 개수
  @Transactional(readOnly = true)
  public List<UnreadResponse> GetUnreadList(Long mateId, Long userId, UnreadRequest request) {
    // request 유효성 검사
    validUnreadRequest(request);

    // 포트폴리오 항목 내 모든 필드 리스트 조회
    List<ChatField> fields = chatFieldRepository.findByOwner_IdAndPortfolioType(mateId,
        request.getPortfolioType());

    // 코멘트가 존재하는 필드가 존재하지 않는 경우 빈 리스트 반환
    if (fields.isEmpty()) {
      return List.of();
    }

    // field Id 목록
    List<Long> fieldIds = fields.stream().map(ChatField::getId).toList();

    // 미읽음 메시지가 있는 field Id 목록
    List<Long> unreadFieldIds = chatMessageRepository.findUnreadFieldIds(fieldIds, userId);
    Set<Long> unreadSet = new HashSet<>(unreadFieldIds);

    List<UnreadResponse> responses = new ArrayList<>(fields.size());
    for (ChatField chatField : fields) {
      TypeEnum portfolioType = chatField.getPortfolioType();
      Long blockId = chatField.getBlockId();
      String fieldKey = chatField.getFieldKey();
      // 필드는 존재하면서 미읽음은 존재하지 않으면 false / 있으면 true
      boolean unread = unreadSet.contains(chatField.getId());

      responses.add(UnreadResponse.from(portfolioType, blockId, fieldKey, unread));
    }

    // blockId 기준 오름차순 정렬
    responses.sort(Comparator.comparing(
        UnreadResponse::getBlockId,
        Comparator.nullsFirst(Long::compare)
    ));
    return responses;
  }

  @Transactional
  protected void markReadUpToFieldId(Long fieldId, Long userId, Long messageId) {
    getReadPointer(fieldId, userId);

    int updated = chatFieldReadRepository.advancePointer(fieldId, userId, messageId, now());
    if (updated == 0) {
      ChatFieldRead row = chatFieldReadRepository.findByField_IdAndUser_Id(fieldId, userId)
          .orElseThrow(() -> new BaseException(CHAT_READ_ERROR));
      row.advanceTo(messageId, now());
      chatFieldReadRepository.save(row);
    }
  }


  /** 헬퍼 메서드 */
  // 포인터가 존재하는지 보장
  private void getReadPointer(Long fieldId, Long userId) {
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

  // 항목-블록-필드명으로 fieldId 조회
  private Long getFieldId(Long userId, CreateChatRequest request) {
    return chatFieldService.getOrCreate(
        userId, request.getPortfolioType(),
        request.getBlockId(),
        request.getFieldKey()).getId();
  }


  // Request 타입별 유효성 검사
  private void validCreateChatRequest(CreateChatRequest request) {
    if (request == null) {
      throw new BaseException(INVALID_CHAT_FORM);
    }
    if (request.getPortfolioType() == null) {
      throw new BaseException(INVALID_TYPE_ENUM);
    }
    if (request.getBlockId() == null || request.getFieldKey() == null || request.getFieldKey()
        .isBlank()) {
      throw new BaseException(INVALID_CHAT_FORM);
    }
  }

  private void validUnreadRequest(UnreadRequest request) {
    if (request == null) {
      throw new BaseException(INVALID_CHAT_FORM);
    }
    if (request.getPortfolioType() == null) {
      throw new BaseException(INVALID_TYPE_ENUM);
    }
  }
}
