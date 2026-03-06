package server.pome.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.jwt.dto.request.DevCreateUserRequest;
import server.pome.jwt.dto.response.DevCreateUserResponse;
import server.pome.jwt.dto.response.UserLoginResponse;
import server.pome.jwt.provider.JwtTokenProvider;
import server.pome.jwt.repository.AuthUserRepository;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.repository.UserRepository;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class DevAuthService {

    private final UserRepository userRepository;
    private final AuthUserRepository authUserRepository;
    private final PortfolioService portfolioService;
    private final JwtTokenProvider jwtTokenProvider;

    // userId로 테스트 access token 발급
    @Transactional(readOnly = true)
    public UserLoginResponse generateTestAccessToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());

        return UserLoginResponse.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .nickName(user.getNickName())
                .accessToken(accessToken)
                .build();
    }

    // 개발용 가짜 유저 생성, 초기 포트폴리오 및 토큰 생성
    public DevCreateUserResponse createMockUser(DevCreateUserRequest request) {
        String userName = resolveUserName(request);
        String nickName = resolveNickName(request);
        String email = resolveEmail(request);
        Long kakaoId = resolveKakaoId();
        boolean matching = request == null || request.getMatching() == null || request.getMatching();

        User newUser = User.builder()
                .userName(userName)
                .nickName(nickName)
                .email(email)
                .kakaoId(kakaoId)
                .matching(matching)
                .likeCount(0)
                .build();

        userRepository.save(newUser);
        portfolioService.createInitialPortfolio(newUser);

        String accessToken = jwtTokenProvider.generateAccessToken(newUser.getId());

        return DevCreateUserResponse.builder()
                .userId(newUser.getId())
                .userName(newUser.getUserName())
                .nickName(newUser.getNickName())
                .email(newUser.getEmail())
                .kakaoId(newUser.getKakaoId())
                .accessToken(accessToken)
                .build();
    }

    // userName 미입력 시 랜덤값 사용
    private String resolveUserName(DevCreateUserRequest request) {
        String candidate = request == null ? null : request.getUserName();
        if (candidate == null || candidate.isBlank()) {
            return "dev-user-" + UUID.randomUUID().toString().substring(0, 8);
        }
        return candidate.trim();
    }

    // 미입력, 중복 시 랜덤 닉네임 자동 생성
    private String resolveNickName(DevCreateUserRequest request) {
        String candidate = request == null ? null : request.getNickName();
        if (candidate != null && !candidate.isBlank() && !userRepository.existsByNickName(candidate.trim())) {
            return candidate.trim();
        }

        while (true) {
            String generated = "dev-" + UUID.randomUUID().toString().substring(0, 8);
            if (!userRepository.existsByNickName(generated)) {
                return generated;
            }
        }
    }

    // 미입력, 중복 시 개발용 이메일 자동 생성
    private String resolveEmail(DevCreateUserRequest request) {
        String candidate = request == null ? null : request.getEmail();
        if (candidate != null && !candidate.isBlank() && !authUserRepository.existsByEmail(candidate.trim())) {
            return candidate.trim();
        }

        while (true) {
            String generated = "dev+" + UUID.randomUUID().toString().replace("-", "") + "@pome.local";
            if (!authUserRepository.existsByEmail(generated)) {
                return generated;
            }
        }
    }

    // DB와 충돌이 나지 않을 때까지 kakaoId 생성
    private Long resolveKakaoId() {
        while (true) {
            long candidate = ThreadLocalRandom.current()
                    .nextLong(100_000_000_000_000L, 1_000_000_000_000_000L);
            if (authUserRepository.findByKakaoId(candidate).isEmpty()) {
                return candidate;
            }
        }
    }
}
