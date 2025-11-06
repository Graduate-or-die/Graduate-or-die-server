package server.pome.user.service;

import static server.pome.global.exception.BaseResponseStatus.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import server.pome.like.dto.response.LikeResponse;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.dto.request.CreateUserRequest;
import server.pome.user.dto.request.UpdateUserRequest;
import server.pome.user.dto.request.UserLoginRequest;
import server.pome.user.dto.response.*;
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

  private final WebClient kakaoWebClient; // HttpClientConfig에서 @Bean 등록된 WebClient 주입

  @Value("${kakao.oauth.client-id}")
  private String kakaoClientId;

  @Value("${kakao.oauth.redirect-uri}")
  private String kakaoRedirectUri;

  @Value("${kakao.oauth.client-secret:}")
  private String kakaoClientSecret;

  // 로그인
  public UserLoginResponse loginWithKakaoCode(String code) {
    if (code == null || code.isBlank()) {
      throw new BaseException(REQUEST_ERROR);
    }

    // 1) 인가코드로 카카오 토큰 교환
    KakaoTokenResponse token = exchangeCodeForToken(code);

    // 2) access_token으로 카카오 유저 정보 조회
    KakaoUserResponse kakaoUser = fetchKakaoUser(token.getAccessToken());

    // 3) DB 매핑 (임시: kakaoId/email 필드가 없다고 가정 → nickname 기반 생성/조회)
    User user = findOrCreateUserFromKakao(kakaoUser);

    // 4)JWT 발급 및 응답 커미션
    // TODO: 팀의 JwtProvider 규격에 맞춰 실제 토큰 발급/반환

    // 임시 로직 (JWT 미발급 상태에서 최소 정보만 반환)
    return UserLoginResponse.builder()
            .userId(user.getId())
            .userName(user.getUserName())
            .nickName(user.getNickName())
            // .accessToken("TEMP_ACCESS_" + user.getId())
            // .refreshToken("TEMP_REFRESH_" + user.getId())
            .build();
  }

  private KakaoTokenResponse exchangeCodeForToken(String code) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", kakaoClientId);
    form.add("redirect_uri", kakaoRedirectUri);
    form.add("code", code);
    if (kakaoClientSecret != null && !kakaoClientSecret.isBlank()) {
      form.add("client_secret", kakaoClientSecret);
    }

    return kakaoWebClient.post()
            .uri("/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(form))
            .retrieve()
            .bodyToMono(KakaoTokenResponse.class)
            .block();
  }

  private KakaoUserResponse fetchKakaoUser(String accessToken) {
    return kakaoWebClient.mutate()
            .baseUrl("https://kapi.kakao.com") // 사용자 정보는 kapi 서버
            .build()
            .get()
            .uri("/v2/user/me")
            .headers(h -> h.setBearerAuth(accessToken))
            .retrieve()
            .bodyToMono(KakaoUserResponse.class)
            .block();
  }

  // DB 매핑 (임시 버전)

  private User findOrCreateUserFromKakao(KakaoUserResponse kakaoUser) {
    String preferNickname = kakaoUser.getProperties() != null
            ? kakaoUser.getProperties().getNickname()
            : "kakao_user";

    // 1) 우선 닉네임으로 존재하면 그 유저 사용 (임시)
    User found = userRepository.findByNickName(preferNickname);
    if (found != null) {
      return found;
    }

    // 2) 없으면 신규 생성 (임시 값 채움)
    String uniqueNick = preferNickname;
    if (userRepository.existsByNickName(preferNickname)) {
      uniqueNick = preferNickname + "-kakao-" + UUID.randomUUID().toString().substring(0, 6);
    }

    User user = new User(
            null,                       // password 없음
            preferNickname,             // userName 임시로 닉네임 사용 (필요 시 profile.nickname / account_name 분리)
            uniqueNick,                 // nickName
            0,                          // likeCount
            true,                       // matching
            null,                       // introduction
            null,                       // job
            null                        // profileImage
    );

    userRepository.save(user);
    // 신규면 포트폴리오 초기화
    portfolioService.createInitialPortfolio(user);

    return user;
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
