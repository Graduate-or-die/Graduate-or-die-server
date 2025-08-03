package server.pome.mate.service;

import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.Mate;
import server.pome.global.domain.User;
import server.pome.global.enums.MateRequestStatus;
import server.pome.global.exception.BaseException;
import server.pome.mate.dto.response.GetMateRequestResponse;
import server.pome.mate.repository.MateRepository;
import server.pome.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class MateService {

  private final UserRepository userRepository;
  private final MateRepository mateRepository;

  // 메이트 신청자 리스트 조회
  @Transactional(readOnly = true)
  public List<GetMateRequestResponse> getMateRequest(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

    // 신청자의 상태가 '대기' 상태인 Mate 리스트 반환
    List<Mate> mateRequestList = mateRepository.findByTargetUserAndStatus(user,
        MateRequestStatus.PENDING);

    // 신청자의 아이디, 닉네임을 추출하여 응답 리스트에 저장
    List<GetMateRequestResponse> responseList = new ArrayList<>();
    for (Mate mate : mateRequestList) {
      User mateUser = mate.getFromUser();
      responseList.add(
          GetMateRequestResponse.from(
              mateUser.getId(),
              mateUser.getNickName()
            // TODO: mateUser.getProfileImage()
          )
      );
    }

    return responseList;
  }
}
