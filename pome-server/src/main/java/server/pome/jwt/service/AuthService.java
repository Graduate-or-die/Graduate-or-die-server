package server.pome.jwt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import server.pome.global.domain.RefreshToken;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.jwt.dto.response.KakaoTokenResponse;
import server.pome.jwt.dto.response.KakaoUserResponse;
import server.pome.jwt.dto.response.LoginTokensResponse;
import server.pome.jwt.dto.response.TokenReissueResponse;
import server.pome.jwt.provider.JwtTokenProvider;
import server.pome.jwt.repository.AuthUserRepository;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static server.pome.global.exception.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthUserRepository authUserRepository;
    private final PortfolioService portfolioService;
    private final WebClient kakaoWebClient; // HttpClientConfig에서 @Bean 등록된 WebClient 주입

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
            Optional<User> byEmail = authUserRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                return byEmail.get().linkKakao(kakaoId, email);
            }
        }

        // email 없으면 ID
        Optional<User> byKakaoId = authUserRepository.findByKakaoId(kakaoId);
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

    public TokenReissueResponse reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BaseException(REQUEST_ERROR);
        }

        // 1) refresh JWT 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BaseException(INVALID_TOKEN);
        }

        // 2) DB에 저장된 refresh 확인
        RefreshToken saved = refreshTokenService.findByTokenOrNull(refreshToken);
        if (saved == null) {
            throw new BaseException(INVALID_TOKEN);
        }

        // 3) 유저 존재 확인
        User user = saved.getUser();

        // 4) 회전: refresh 새로 발급 + DB 갱신
        String newRefresh = jwtTokenProvider.generateRefreshToken(user.getId());
        LocalDateTime newExpireAt = jwtTokenProvider.calcRefreshExpireAt();
        saved.rotate(newRefresh, newExpireAt);

        // 5) access 새로 발급
        String newAccess = jwtTokenProvider.generateAccessToken(user.getId());

        return TokenReissueResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .build();
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        refreshTokenService.deleteByToken(refreshToken);
    }
}
