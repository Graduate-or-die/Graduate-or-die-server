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
import server.pome.jwt.dto.response.LoginTokensResponse;
import server.pome.jwt.provider.JwtTokenProvider;
import server.pome.jwt.service.RefreshTokenService;
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
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;

  @Value("${kakao.oauth.client-id}")
  private String kakaoClientId;

  @Value("${kakao.oauth.redirect-uri}")
  private String kakaoRedirectUri;

  @Value("${kakao.oauth.client-secret:}")
  private String kakaoClientSecret;

  // 로그인
  public LoginTokensResponse loginWithKakaoCode(String code) {
    if (code == null || code.isBlank()) {
      log.warn("[LOGIN] Blank authorization code");
      throw new BaseException(REQUEST_ERROR);
    }

    // 1) 인가코드로 카카오 토큰 교환
    KakaoTokenResponse token = exchangeCodeForToken(code);

    // 2) access_token으로 카카오 유저 정보 조회
    KakaoUserResponse kakaoUser = fetchKakaoUser(token.getAccessToken());

    // 3) DB 매핑
    User user = findOrCreateUserFromKakao(kakaoUser);

    // 4)JWT 발급
    String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

    // refresh 만료시각 계산 (DB 저장)
    java.time.LocalDateTime refreshExpireAt =
            java.time.LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenValidityInSeconds());

    // DB 저장
    refreshTokenService.save(user, refreshToken, refreshExpireAt);

    return LoginTokensResponse.builder()
            .userId(user.getId())
            .userName(user.getUserName())
            .nickName(user.getNickName())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
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

  private User findOrCreateUserFromKakao(KakaoUserResponse kakaoUser) {
    Long kakaoId = kakaoUser.getId();
    String email = (kakaoUser.getKakaoAccount() != null)
            ? kakaoUser.getKakaoAccount().getEmail()
            : null;
    String nickname = (kakaoUser.getProperties() != null)
            ? kakaoUser.getProperties().getNickname()
            : "kakao_user";

    // email 우선
    if (email != null && !email.isBlank()) {
      Optional<User> byEmail = userRepository.findByEmail(email);
      if (byEmail.isPresent()) {
        return byEmail.get().linkKakao(kakaoId, email);
      }
    }

    // email 없으면 ID
    Optional<User> byKakaoId = userRepository.findByKakaoId(kakaoId);
    if (byKakaoId.isPresent()) {
      return byKakaoId.get().linkKakao(kakaoId, email);
    }

    // 둘 다 없으면 신규 생성 (임시 값 채움)
    String uniqueNick = nickname;
    if (userRepository.existsByNickName(nickname)) {
      uniqueNick = nickname + "-kakao-" + UUID.randomUUID().toString().substring(0, 6);
    }

    User newUser = User.builder()
            .userName(nickname)
            .nickName(uniqueNick)
            .likeCount(0)
            .matching(true)
            .kakaoId(kakaoId)
            .email(email) // null 허용
            .build();

    userRepository.save(newUser);
    portfolioService.createInitialPortfolio(newUser);

    return newUser;
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
