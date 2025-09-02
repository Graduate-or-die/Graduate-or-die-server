package server.pome.chat.service;

import static server.pome.global.exception.BaseResponseStatus.INVALID_CHAT_FORM;
import static server.pome.global.exception.BaseResponseStatus.INVALID_TYPE_ENUM;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.chat.repository.ChatFieldRepository;
import server.pome.global.domain.ChatField;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.type.TypeEnum;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatFieldService {

  private final ChatFieldRepository chatFieldRepository;
  private final UserRepository userRepository;

  // 생성 가능한 최대 블록 수
  private static int MAX_BLOCK_ID = 50;

  @Transactional
  public ChatField getOrCreate(Long ownerId, TypeEnum portfolioType, Long blockId,
      String fieldKey) {

    // 유효성 검증
    if (portfolioType == null) {
      throw new BaseException(INVALID_TYPE_ENUM);
    }

    if (blockId == null || blockId > MAX_BLOCK_ID) {
      throw new BaseException(INVALID_CHAT_FORM);
    }

    if (fieldKey == null || fieldKey.isBlank()) {
      throw new BaseException(INVALID_CHAT_FORM);
    }

    return chatFieldRepository.findByOwner_IdAndPortfolioTypeAndBlockIdAndFieldKey(ownerId,
            portfolioType, blockId, fieldKey)
        .orElseGet(() -> {
          User ownerRef = userRepository.getReferenceById(ownerId);
          return chatFieldRepository.save(
              new ChatField(ownerRef, portfolioType, blockId, fieldKey));
        });
    }

}
