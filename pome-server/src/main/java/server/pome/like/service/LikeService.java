package server.pome.like.service;

import static server.pome.global.exception.BaseResponseStatus.ALREADY_LIKED;
import static server.pome.global.exception.BaseResponseStatus.ALREADY_UNLIKED;
import static server.pome.global.exception.BaseResponseStatus.CANNOT_LIKE_SELF;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.exception.BaseException;
import server.pome.like.dto.response.LikeResponse;
import server.pome.like.repository.LikeRepository;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;
  private final UserRepository userRepository;

  // 좋아요 추가
  @Transactional
  public LikeResponse like(Long mateId, Long userId) {
    // 스스로 좋아요 금지
    ensureNoSelfLike(mateId, userId);

    //유효한 사용자인지 검사
    ensureValidUser(mateId, userId);

    // 신규 저장 = 1, 중복 저장 = 0 반환
    int saved = likeRepository.saveIfNotExists(userId, mateId);
    // 신규 저장인 경우에만 저장 (중복 시 저장 안함)
    if (saved == 1) {
      userRepository.increaseLikeCount(mateId);
    } else {
      throw new BaseException(ALREADY_LIKED);
    }

    int likeCount = userRepository.findLikeCountById(mateId);
    return LikeResponse.toEntity(mateId, likeCount);
  }

  @Transactional
  public LikeResponse unlike(Long mateId, Long userId) {
    // 스스로 좋아요 금지
    ensureNoSelfLike(mateId, userId);

    //유효한 사용자인지 검사
    ensureValidUser(mateId, userId);

    // 신규 삭제 = 1, 중복 삭제 = 0 반환
    int deleted = likeRepository.deleteByFromUser_IdAndTargetUser_Id(userId, mateId);
    // 신규 삭제인 경우에만 저장 (중복 시 저장 안함)
    if (deleted == 1) {
      userRepository.decreaseLikeCount(mateId);
    } else {
      throw new BaseException(ALREADY_UNLIKED);
    }

    int likeCount = userRepository.findLikeCountById(mateId);
    return LikeResponse.toEntity(mateId, likeCount);
  }

  /** 헬퍼 메서드 */
  private void ensureNoSelfLike(Long aId, Long bId) {
    if (aId.equals(bId)) {
      throw new BaseException(CANNOT_LIKE_SELF);
    }
  }

  // 유효한 사용자인지 검사
  private void ensureValidUser(Long aId, Long bId) {
    if (!userRepository.existsById(aId) || !userRepository.existsById(bId)) {
      throw new BaseException(USER_NOT_FOUND);
    }
  }
}
