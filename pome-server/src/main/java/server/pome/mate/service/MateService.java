package server.pome.mate.service;

import static server.pome.global.enums.MateRequestStatus.ACCEPTED;
import static server.pome.global.enums.MateRequestStatus.PENDING;
import static server.pome.global.enums.MateRequestStatus.REJECTED;
import static server.pome.global.enums.MateRequestStatus.UNMATCHED;
import static server.pome.global.exception.BaseResponseStatus.ALREADY_HAVE_MATE;
import static server.pome.global.exception.BaseResponseStatus.ALREADY_REQUEST_MATE;
import static server.pome.global.exception.BaseResponseStatus.CANNOT_MATE_SELF_REQUEST;
import static server.pome.global.exception.BaseResponseStatus.CONFLICT_STATE;
import static server.pome.global.exception.BaseResponseStatus.MATCHING_DISABLED;
import static server.pome.global.exception.BaseResponseStatus.NOT_MATCHED_MATE;
import static server.pome.global.exception.BaseResponseStatus.PROPOSER_NOT_FOUND;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.Mate;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.matching.application.UserMatchService;
import server.pome.mate.dto.response.GetMateRequestResponse;
import server.pome.mate.repository.MateRepository;
import server.pome.message.service.MessageRoomService;
import server.pome.user.repository.UserRepository;


@RequiredArgsConstructor
@Service
public class MateService {

  private final UserRepository userRepository;
  private final MateRepository mateRepository;
  private final MessageRoomService messageRoomService;
  private final UserMatchService userMatchService;

  // 메이트 신청자 리스트 조회
  @Transactional(readOnly = true)
  public List<GetMateRequestResponse> getMateRequest(Long userId) {
    User user = findUserById(userId);

    // 신청자의 상태가 '대기' 상태인 Mate 리스트 반환
    List<Mate> mateRequestList = mateRepository.findByTargetUserAndStatus(user, PENDING);

    // 신청자의 아이디, 닉네임을 추출하여 응답 리스트에 저장
    List<GetMateRequestResponse> responseList = new ArrayList<>();

    for (Mate mate : mateRequestList) {
      responseList.add(GetMateRequestResponse.from(mate));
    }

    return responseList;
  }

  // 메이트 신청
  @Transactional
  public String requestMate(Long mateId, Long userId) {
    // 본인에게 메이트 API 호출 금지
    ensureNoSelfMate(mateId, userId);

    User[] pair = lockPair(mateId, userId);
    User mateUser = pair[0]; // 신청 받은 유저
    User user = pair[1]; // 신청 보낸 유저


    // 이미 신청 상태인 경우
    boolean alreadyRequest = mateRepository
        .existsByFromUserAndTargetUserAndStatus(user, mateUser, PENDING);
    if (alreadyRequest) {
      throw new BaseException(ALREADY_REQUEST_MATE);
    }

    // 신규 메이트 신청
    ensureNoMate(user, mateUser);
    ensureCanMatching(user, mateUser);

    // 신청 대기(PENDING) 상태로 저장
    Mate newMate = new Mate(null, mateUser, user, PENDING);
    mateRepository.save(newMate);

    return userId + "가 " + mateId + "에게 메이트를 신청했습니다";
  }

  // 메이트 거절
  @Transactional
  public String rejectMate(Long mateId, Long userId) {
    // 본인에게 메이트 API 호출 금지
    ensureNoSelfMate(mateId, userId);

    User[] pair = lockPair(mateId, userId);
    User mateUser = pair[0];
    User user = pair[1];


    // 메이트의 신청자 리스트에 mateUser가 없는 경우
    boolean isPending = mateRepository.existsByFromUserAndTargetUserAndStatus(mateUser, user,
        PENDING);
    if (!isPending) {
      throw new BaseException(PROPOSER_NOT_FOUND);
    }

    int updateMate = mateRepository.updateStatusTo(mateUser, user, PENDING, REJECTED);

    // 업데이트가 반영되지 않으면 예외 처리
    if (updateMate == 0) {
      throw new BaseException(PROPOSER_NOT_FOUND);
    }

    return userId + "의 신청자 리스트에서 " + mateId + "를 제거했습니다.";
  }

  // 메이트 매칭
  @Transactional
  public String matchMate(Long mateId, Long userId) {
    // 본인에게 메이트 API 호출 금지
    ensureNoSelfMate(mateId, userId);

    User[] pair = lockPair(mateId, userId);
    User mateUser = pair[0];
    User user = pair[1];

    // 매칭 전 검사
    ensureNoMate(user, mateUser);
    ensureCanMatching(user, mateUser);

    // 신청자 리스트에 user가 있는지 검사
    boolean requested = mateRepository.existsByFromUserAndTargetUserAndStatus(mateUser, user,
        PENDING);
    if (!requested) {
      throw new BaseException(PROPOSER_NOT_FOUND);
    }

    // 양방향으로 매칭 상태 변경(PENDING -> ACCEPTED)
    int updated = mateRepository.updateStatusEitherDirection(userId, mateId, PENDING, ACCEPTED);

    // mate는 이미 user에게 요청한 상태이므로 'user -> mate' 없던 케이스만 검사 보완
    if (updated == 0) {
      throw new BaseException(CONFLICT_STATE);
    }

    // 확정 매칭 기록
    userMatchService.recordMatch(userId, mateId);

    // 채팅방 생성
    messageRoomService.getOrCreateMessageRoom(userId, mateId);

    return userId + "와 " + mateId + "가 매칭되었습니다.";
  }

  // 메이트 해제
  @Transactional
  public String unmatchMate(Long userId) {

    Long mateId = mateRepository
            .findMateIdByUserIdAndStatus(userId, ACCEPTED)
            .orElseThrow(() -> new BaseException(NOT_MATCHED_MATE));

    User[] pair = lockPair(mateId, userId);
    User mateUser = pair[0];
    User user = pair[1];


    // 먼저 매칭 상태인지 확인
    boolean matched = mateRepository.existsByFromUserAndTargetUserAndStatus(user, mateUser, ACCEPTED)
        || mateRepository.existsByFromUserAndTargetUserAndStatus(mateUser, user, ACCEPTED);

    if (!matched) {
      throw new BaseException(NOT_MATCHED_MATE);
    }

    // ACCEPTED -> UNMATCHED 상태 변경 (양방향)
    int updated = mateRepository.updateStatusEitherDirection(userId, mateId, ACCEPTED, UNMATCHED);

    if (updated == 0) {
      throw new BaseException(CONFLICT_STATE);
    }

    userMatchService.removeMatch(userId, mateId);

    // TODO: 채팅방, 코멘트, 채팅, 메시지 삭제

    return userId + "와 " + mateId + "의 매칭을 해제했습니다.";
  }


  /**  헬퍼 메서드 */
  private User findUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    return user;
  }

  private User findUserByIdWithLock(Long id) {
    return userRepository.findUserByIdWithLock(id)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
  }

  // 교착상태 방지 유저 락
  private User[] lockPair(Long aId, Long bId) {
    Long min = Math.min(aId, bId);
    Long max = Math.max(aId, bId);
    User first = findUserByIdWithLock(min);
    User second = findUserByIdWithLock(max);
    return (aId.equals(min)) ? new User[]{first, second} : new User[]{second, first};
  }

  // 메이트가 이미 존재하는지 않도록 검사
  private void ensureNoMate(User a, User b) {
    if (mateRepository.existsAcceptedByUser(a) || mateRepository.existsAcceptedByUser(b)) {
      throw new BaseException(ALREADY_HAVE_MATE);
    }
  }

  // 매칭 활성화 여부 검사
  private void ensureCanMatching(User a, User b) {
    if (!a.getMatching() || !b.getMatching()) {
      throw new BaseException(MATCHING_DISABLED);
    }
  }

  private void ensureNoSelfMate(Long aId, Long bId) {
    if (aId.equals(bId)) {
      throw new BaseException(CANNOT_MATE_SELF_REQUEST);
    }
  }

  // 메이트 여부 검증
  public boolean isAcceptedMates(Long u1, Long u2) {
    return mateRepository.isAcceptedMates(u1, u2, ACCEPTED);
  }
}
