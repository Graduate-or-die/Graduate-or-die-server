package server.pome.matching.application.query;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.User;
import server.pome.like.repository.LikeRepository;
import server.pome.matching.application.recommend.MateRecommendationService;
import server.pome.matching.dto.response.RecommendUserListResponse;
import server.pome.matching.dto.response.RecommendUserResponse;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MatchingUserQueryService {

  private final MateRecommendationService mateRecommendationService;
  private final UserRepository userRepository;
  private final LikeRepository likeRepository;

  // 태그 유사도가 높은 유저 조회
  @Transactional(readOnly = true)
  public RecommendUserListResponse getRecommendUser(long userId) {
    // 추천 점수 계산
    var candidates = mateRecommendationService.recommend(userId);
    List<Long> candidateIds = candidates.stream()
        .map(candidate -> candidate.userId())
        .toList();

    // 사용자 정보 조회
    Map<Long, User> userMap = userRepository.findAllById(candidateIds).stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));
    Set<Long> likedUserIds = candidateIds.isEmpty()
        ? Set.of()
        : likeRepository.findLikedTargetUserIds(userId, candidateIds).stream()
            .collect(Collectors.toSet());

    // 사용자 정보와 퍼센트 값으로 응답 생성
    List<RecommendUserResponse> users = candidates.stream()
        .map(candidate -> {
          User candidateUser = userMap.get(candidate.userId());
          if (candidateUser == null) {
            return null;
          }
          return RecommendUserResponse.from(candidateUser,
              likedUserIds.contains(candidate.userId()),
              toPercent(candidate.score()));
        })
        .filter(Objects::nonNull)
        .toList();

    return RecommendUserListResponse.from(userId, users);
  }

  private int toPercent(double score) {
    double clamped = Math.max(0.0, Math.min(1.0, score));
    return (int) Math.round(clamped * 100);
  }
}
