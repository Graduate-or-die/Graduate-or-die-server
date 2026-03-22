package server.pome.jwt.service;

import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.jwt.dto.request.DevCreateUserRequest;
import server.pome.jwt.dto.response.DevCreateUserResponse;
import server.pome.jwt.dto.response.UserLoginResponse;
import server.pome.jwt.provider.JwtTokenProvider;
import server.pome.jwt.repository.AuthUserRepository;
import server.pome.matching.application.event.TagsUpdatedEvent;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DevAuthService {

    private final UserRepository userRepository;
    private final AuthUserRepository authUserRepository;
    private final PortfolioService portfolioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher publisher;

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

    // 개발용 가짜 유저 생성, 초기 포트폴리오, 태그, 토큰 생성
    public DevCreateUserResponse createMockUser(DevCreateUserRequest request) {
        String userName = resolveUserName(request);
        String nickName = resolveNickName(request);
        String email = resolveEmail(request);
        Long kakaoId = resolveKakaoId();
        boolean matching = request == null || request.getMatching() == null || request.getMatching();
        List<String> tags = resolveTags(request);

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

        // 임의 태그 저장
        newUser.updateTags(tags, 0L);
        publisher.publishEvent(new TagsUpdatedEvent(
                newUser.getId(),
                "mock-user:" + newUser.getId() + ":" + System.currentTimeMillis()
        ));

        String accessToken = jwtTokenProvider.generateAccessToken(newUser.getId());

        return DevCreateUserResponse.builder()
                .userId(newUser.getId())
                .userName(newUser.getUserName())
                .nickName(newUser.getNickName())
                .email(newUser.getEmail())
                .kakaoId(newUser.getKakaoId())
                .tags(newUser.getTags())
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

    private List<String> resolveTags(DevCreateUserRequest request) {
        // 입력한 경우 정규화해서 저장
        List<String> requestedTags = request == null ? null : request.getTags();
        if (requestedTags != null && !requestedTags.isEmpty()) {
            LinkedHashSet<String> normalized = new LinkedHashSet<>();
            for (String tag : requestedTags) {
                if (tag == null) {
                    continue;
                }
                String trimmed = tag.trim();
                if (trimmed.isBlank()) {
                    continue;
                }
                normalized.add(trimmed.startsWith("#") ? trimmed : "#" + trimmed);
            }
            if (!normalized.isEmpty()) {
                return new ArrayList<>(normalized);
            }
        }

        // 입력하지 않은 경우 랜덤 생성
        return generateRandomTags();
    }

    private List<String> generateRandomTags() {
        int size = ThreadLocalRandom.current().nextInt(4, 7);
        List<String> shuffled = new ArrayList<>(MOCK_TAG_POOL);
        Collections.shuffle(shuffled);
        return new ArrayList<>(shuffled.subList(0, size));
    }

    private static final List<String> MOCK_TAG_POOL = List.of(
        "#프론트엔드개발자",
        "#백엔드개발자",
        "#앱개발지망생",
        "#UX디자이너",
        "#UI디자이너",
        "#브랜드디자이너",
        "#서비스기획자",
        "#프로덕트매니저",
        "#콘텐츠마케터",
        "#퍼포먼스마케터",
        "#데이터분석가",
        "#데이터기획러",
        "#운영기획자",
        "#채용담당자",
        "#세일즈기획",
        "#프로젝트마스터",
        "#협업중심",
        "#배포경험러",
        "#대기업러버",
        "#스타트업체질",
        "#디버깅집착러",
        "#기획도가능",
        "#트렌드캐처",
        "#회의정리장인",
        "#마감수호자",
        "#실험설계러"
    );
}
