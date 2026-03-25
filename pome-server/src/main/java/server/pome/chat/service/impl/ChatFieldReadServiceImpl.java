package server.pome.chat.service.impl;

import static java.time.LocalDateTime.now;
import static server.pome.global.exception.BaseResponseStatus.CHAT_READ_ERROR;
import static server.pome.global.exception.BaseResponseStatus.INVALID_REQUEST_FORM;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_PARTICIPANT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.dto.request.ReadRequest;
import server.pome.chat.dto.request.UnreadRequest;
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
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.mate.service.MateService;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatFieldReadServiceImpl implements ChatFieldReadService {

  private final ChatFieldReadRepository chatFieldReadRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatFieldRepository chatFieldRepository;
  private final UserRepository userRepository;
  private final ChatFieldService chatFieldService;
  private final MateService mateService;

  // 해당 필드의 최신 메시지까지 읽음 처리
  @Transactional
  @Override
  public void markReadUpToLatest(Long portfolioOwnerId, Long userId, ReadRequest request) {
    // 참여 권한 검증
    validateParticipant(portfolioOwnerId, userId);

    // 가장 최신 메시지 ID 조회 (없으면 0 반환)
    ChatField currentField = chatFieldService.getOrCreate(
        portfolioOwnerId,
        request.getTypeId(),
        request.getBlockId(),
        request.getFieldKey());

    List<ChatField> syncedFields = getSyncedFields(portfolioOwnerId, request);
    if (syncedFields.stream().noneMatch(field -> field.getId().equals(currentField.getId()))) {
      syncedFields.add(currentField);
    }

    // 최신 메시지까지 읽음 표시
    for (ChatField field : syncedFields) {
      markFieldReadUpToLatest(field.getId(), userId);
    }
  }

  // 특정 메시지까지 읽음 처리
  @Transactional
  @Override
  public void markReadUpTo(Long portfolioOwnerId, Long userId, Long messageId, ReadRequest request) {
    // 참여 권한 검증
    validateParticipant(portfolioOwnerId, userId);

    ChatField currentField = chatFieldService.getOrCreate(
        portfolioOwnerId,
        request.getTypeId(),
        request.getBlockId(),
        request.getFieldKey());

    if (messageId == null || messageId <= 0L) {
      getReadPointer(currentField.getId(), userId);
      throw new BaseException(INVALID_REQUEST_FORM);
    }

    // 지정한 메시지까지 읽음 처리
    markReadUpToFieldId(currentField.getId(), userId, messageId);

    for (ChatField field : getSyncedFields(portfolioOwnerId, request)) {
      if (field.getId().equals(currentField.getId())) {
        continue;
      }
      markFieldReadUpToLatest(field.getId(), userId);
    }
  }

  // 타입별 필드들의 미읽음 여부 조회
  @Transactional(readOnly = true)
  public List<UnreadResponse> GetUnreadList(Long portfolioOwnerId, Long userId, UnreadRequest request) {
    // 참여 권한 검증
    validateParticipant(portfolioOwnerId, userId);

    // 포트폴리오 항목 내 모든 필드 리스트 조회
    List<ChatField> fields = chatFieldRepository.findByOwner_IdAndPortfolioType(
        portfolioOwnerId, TypeEnum.fromId(request.getTypeId()));

    // 코멘트가 존재하는 필드가 존재하지 않는 경우 빈 리스트 반환
    if (fields.isEmpty()) {
      return List.of();
    }

    // field ID 목록
    List<Long> fieldIds = fields.stream().map(ChatField::getId).toList();

    // 미읽음 메시지가 있는 field ID 목록
    List<Long> unreadFieldIds = chatMessageRepository.findUnreadFieldIds(fieldIds, userId);
    Set<Long> unreadSet = new HashSet<>(unreadFieldIds);

    // 필드는 존재하면서 미읽음은 존재하지 않으면 false / 있으면 true
    List<UnreadResponse> responses = new ArrayList<>(fields.size());
    for (ChatField chatField : fields) {
      responses.add(UnreadResponse.from(
          chatField.getPortfolioType().getId(),
          chatField.getBlockId(),
          chatField.getFieldKey(),
          unreadSet.contains(chatField.getId())
      ));
    }

    // blockId 기준 오름차순 정렬
    responses.sort(Comparator.comparing(
        UnreadResponse::getBlockId,
        Comparator.nullsFirst(Long::compare)
    ));
    return responses;
  }

  // 읽음 포인터를 messageId까지 전진
  @Transactional
  protected void markReadUpToFieldId(Long fieldId, Long userId, Long messageId) {
    // 포인터 row가 없으면 생성
    getReadPointer(fieldId, userId);

    int updated = chatFieldReadRepository.advancePointer(fieldId, userId, messageId, now());
    if (updated == 0) {
      // 동시성 상황에서 update가 실패하면 row를 다시 읽어 전진
      ChatFieldRead row = chatFieldReadRepository.findByField_IdAndUser_Id(fieldId, userId)
          .orElseThrow(() -> new BaseException(CHAT_READ_ERROR));
      row.advanceTo(messageId, now());
      chatFieldReadRepository.save(row);
    }
  }

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

  // 동시성 충돌 시 포인터 다시 조회
  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  protected ChatFieldRead reloadRow(Long fieldId, Long userId) {
    return chatFieldReadRepository.findByField_IdAndUser_Id(fieldId, userId)
        .orElseThrow(() -> new BaseException(CHAT_READ_ERROR));
  }

  private void markFieldReadUpToLatest(Long fieldId, Long userId) {
    Long latestId = chatMessageRepository.findTopByField_IdOrderByIdDesc(fieldId)
        .map(ChatMessage::getId)
        .orElse(0L);

    if (latestId <= 0L) {
      getReadPointer(fieldId, userId);
      return;
    }

    markReadUpToFieldId(fieldId, userId, latestId);
  }

  private List<ChatField> getSyncedFields(Long portfolioOwnerId, ReadRequest request) {
    TypeEnum portfolioType = TypeEnum.fromId(request.getTypeId());
    Set<String> syncedKeys = getSyncedFieldKeys(request.getFieldKey());

    return chatFieldRepository.findByOwner_IdAndPortfolioType(portfolioOwnerId, portfolioType)
        .stream()
        .filter(field -> request.getBlockId().equals(field.getBlockId()))
        .filter(field -> syncedKeys.contains(field.getFieldKey()))
        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
  }

  private Set<String> getSyncedFieldKeys(String fieldKey) {
    return SYNCED_FIELD_KEYS.getOrDefault(fieldKey, Set.of(fieldKey));
  }

  // 포트폴리오 본인 또는 매칭된 메이트만 읽음 상태를 조회/갱신 가능
  private void validateParticipant(Long portfolioOwnerId, Long userId) {
    if (!portfolioOwnerId.equals(userId) && !mateService.isAcceptedMates(userId, portfolioOwnerId)) {
      throw new BaseException(USER_NOT_PARTICIPANT);
    }
  }

  private static final Map<String, Set<String>> SYNCED_FIELD_KEYS = Map.ofEntries(
      Map.entry("experienceStartAt", Set.of("experienceStartAt", "experienceEndAt")),
      Map.entry("experienceEndAt", Set.of("experienceStartAt", "experienceEndAt")),
      Map.entry("activityStartAt", Set.of("activityStartAt", "activityEndAt")),
      Map.entry("activityEndAt", Set.of("activityStartAt", "activityEndAt")),
      Map.entry("qualificationStartAt", Set.of("qualificationStartAt", "qualificationEndAt")),
      Map.entry("qualificationEndAt", Set.of("qualificationStartAt", "qualificationEndAt")),
      Map.entry("projectStartAt", Set.of("projectStartAt", "projectEndAt")),
      Map.entry("projectEndAt", Set.of("projectStartAt", "projectEndAt"))
  );
}
