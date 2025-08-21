package server.pome.user.service;

import static server.pome.global.exception.BaseResponseStatus.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.like.dto.response.LikeResponse;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.dto.request.CreateUserRequest;
import server.pome.user.dto.request.UpdateUserRequest;
import server.pome.user.dto.request.UserLoginRequest;
import server.pome.user.dto.response.CreateUserResponse;
import server.pome.user.dto.response.GetUserResponse;
import server.pome.user.dto.response.UpdateUserResponse;
import server.pome.user.dto.response.UserLoginResponse;
import server.pome.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final PortfolioRepository portfolioRepository;
  private final PortfolioService portfolioService;

  // 회원가입 (API 테스트용 임시 코드)
  public CreateUserResponse createUser(CreateUserRequest request) {
    // 중복 닉네임 방지
    if (userRepository.existsByNickName(request.getNickName())) {
      throw new BaseException(DUPLICATE_USER);
    }
    User user = new User(
        request.getPassword(),
        request.getUserName(),
        request.getNickName(),
        0, // likeCount
        true, //matching
        null, // introduction
        null, // job
        null // profileImage
    );

    userRepository.save(user);
    portfolioService.createInitialPortfolio(user);

    return CreateUserResponse.builder()
        .userId(user.getId())
        .userName(user.getUserName())
        .nickName(user.getNickName())
        .build();
  }

  // 로그인
  public UserLoginResponse login(UserLoginRequest request) {
    // TODO: 소셜 로그인 구현
    return null;
  }

  // 회원 정보 조회
  public GetUserResponse getUserInfo(Long userId) {
    User user = findUserById(userId);
    return buildUserResponse(user);
  }

  // 회원 정보 수정
  public UpdateUserResponse updateUserInfo(Long userId, UpdateUserRequest request) {
    User user = findUserById(userId);

    // request에 유효한 값이 있는 경우 저장 (없다면 기존 값 사용)
    String name = Optional.ofNullable(request.getUserName()).orElse(user.getUserName());
    String nickname = Optional.ofNullable(request.getNickName()).orElse(user.getNickName());
    boolean matching = Optional.ofNullable(request.getMatching()).orElse(user.getMatching());
    String introduction = Optional.ofNullable(request.getIntroduction()).orElse(user.getIntroduction());
    String job = Optional.ofNullable(request.getJob()).orElse(user.getJob());

    // 회원 정보 수정
    user.updateUserInfo(name, nickname, matching, introduction, job);

    return UpdateUserResponse.from(user);
  }

  // 회원 검색
  public GetUserResponse searchUser (Long userId, String name) {
    User user = findUserById(userId);

    User searchMate = userRepository.findByNickName(name);
    if (searchMate == null) {
      throw new BaseException(USER_NOT_FOUND);
    }

    if (user.getId().equals(searchMate.getId())) {
      throw new BaseException(CANNOT_MATE_SELF_REQUEST);
    }

    return buildUserResponse(searchMate);
  }

  // 공통 응답 생성 메서드
  private GetUserResponse buildUserResponse(User user) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(user.getId());
    List<String> tags = (portfolio != null) ? portfolio.getTag() : new ArrayList<>();

    return GetUserResponse.from(user, tags);
  }

  // 유저 조회 메서드
  private User findUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
  }
}
