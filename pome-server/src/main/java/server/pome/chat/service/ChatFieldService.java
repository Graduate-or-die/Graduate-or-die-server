package server.pome.chat.service;

import static server.pome.global.exception.BaseResponseStatus.INVALID_REQUEST_FORM;
import static server.pome.global.exception.BaseResponseStatus.INVALID_TYPE_ENUM;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.repository.ChatFieldRepository;
import server.pome.global.domain.ChatField;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.portfolio.type.TypeEnum;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatFieldService {

  private final ChatFieldRepository chatFieldRepository;
  private final UserRepository userRepository;

  // 생성 가능한 최대 블록 수
  private static int MAX_BLOCK_ID = 50;

  // 채팅방 생성 또는 조회
  @Transactional
  public ChatField getOrCreate(Long ownerId, TypeEnum portfolioType, Long blockId,
      String fieldKey) {

    // 유효성 검증
    if (portfolioType == null) {
      throw new BaseException(INVALID_TYPE_ENUM);
    }

    if (blockId == null || blockId > MAX_BLOCK_ID) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }

    if (fieldKey == null || fieldKey.isBlank()) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }

    // 포트폴리오 소유자 ID - 항목 - 블록ID - 필드명으로 field 생성 또는 조회
    return chatFieldRepository.findByOwner_IdAndPortfolioTypeAndBlockIdAndFieldKey(ownerId,
            portfolioType, blockId, fieldKey)
        .orElseGet(() -> {

          // 조회 실패 시 생성
          User ownerRef = userRepository.getReferenceById(ownerId);
          return chatFieldRepository.save(
              new ChatField(ownerRef, portfolioType, blockId, fieldKey));
        });
    }

}
