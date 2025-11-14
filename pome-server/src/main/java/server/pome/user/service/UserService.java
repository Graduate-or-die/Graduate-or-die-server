package server.pome.user.service;

import static server.pome.global.exception.BaseResponseStatus.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.dto.request.UpdateUserRequest;
import server.pome.user.dto.response.*;
import server.pome.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final PortfolioRepository portfolioRepository;
  private final PortfolioService portfolioService;
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
      log.warn("[LOGIN] Blank authorization code");
      throw new BaseException(REQUEST_ERROR);
    }

    // 1) 인가코드로 카카오 토큰 교환
    KakaoTokenResponse token = exchangeCodeForToken(code);

    // 2) access_token으로 카카오 유저 정보 조회
    KakaoUserResponse kakaoUser = fetchKakaoUser(token.getAccessToken());

    // 이메일 유무 관계 없이 처리
    String email = (kakaoUser.getKakaoAccount() != null)
            ? kakaoUser.getKakaoAccount().getEmail()
            : null;

    // 3) DB 매핑
    User user = findOrCreateUserFromKakao(kakaoUser, email);

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

  private User findOrCreateUserFromKakao(KakaoUserResponse kakaoUser, String email) {
    Long kakaoId = kakaoUser.getId();
    String nickname = (kakaoUser.getProperties() != null)
            ? kakaoUser.getProperties().getNickname()
            : "kakao_user";

    // email 우선
    if (email != null && !email.isBlank()) {
      Optional<User> userOpt = userRepository.findByEmail(email);
      if (userOpt.isPresent()) {
        return userOpt.get();
      }
    }

    // email 없으면 ID
    Optional<User> userOpt2 = userRepository.findByKakaoId(kakaoId);
    if (userOpt2.isPresent()) {
      return userOpt2.get();
    }

    // 둘 다 없으면 신규 생성 (임시 값 채움)
    String uniqueNick = nickname;
    if (userRepository.existsByNickName(nickname)) {
      uniqueNick = nickname + "-kakao-" + UUID.randomUUID().toString().substring(0, 6);
    }

    User user = User.builder()
            .userName(nickname)
            .nickName(uniqueNick)
            .likeCount(0)
            .matching(true)
            .kakaoId(kakaoId)
            .email(email) // null 허용
            .build();

    user.linkKakao(kakaoUser.getId(), email);
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
