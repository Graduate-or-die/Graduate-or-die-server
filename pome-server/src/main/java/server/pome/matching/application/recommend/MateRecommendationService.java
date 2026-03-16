package server.pome.matching.application.recommend;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.matching.application.UserMatchService;
import server.pome.matching.dto.response.RecommendCandidateDTO;
import server.pome.vector.domain.Vector;
import server.pome.vector.infrastructure.vectorstore.VectorStoreClient;
import server.pome.vector.infrastructure.vectorstore.VectorStoreNamespace;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.SearchHit;

@Service
@RequiredArgsConstructor
public class MateRecommendationService {

  private static final int TOP_RECOMMENDATION_LIMIT = 10;
  private static final int SEARCH_CANDIDATE_LIMIT = 100;

  private final VectorStoreClient vectorStoreClient;
  private final UserMatchService userMatchService;

  public List<RecommendCandidateDTO> recommend(long me) {
    // 자신의 벡터를 Qdrant에서 불러와 유사 사용자 검색 기준으로 사용
    var myVector = vectorStoreClient.getVector(VectorStoreNamespace.MATCHING_USERS, me);

    // 벡터가 없으면 추천 안함 (정책에 따라 수정 필요)
    if (myVector.isEmpty()) {
      return List.of();
    }

    Vector vector = new Vector(myVector.get()).validateDimension(
        VectorStoreNamespace.MATCHING_USERS
    );

    // 추천 유저 후보군 검색
    List<SearchHit> hits = vectorStoreClient.search(
        VectorStoreNamespace.MATCHING_USERS,
        vector.asList(),
        SEARCH_CANDIDATE_LIMIT
    );

    List<Long> ids = hits.stream()
        .map(SearchHit::id)
        .toList();

    // 매칭 정책상 추천 가능한 유저만 남김
    Set<Long> eligible = userMatchService.filterEligibleCandidates(me, ids);

    List<RecommendCandidateDTO> out = new ArrayList<>();
    for (SearchHit hit : hits) {
      if (eligible.contains(hit.id())) {
        out.add(new RecommendCandidateDTO(hit.id(), hit.score()));
        // 점수 순서를 유지하면서 상위 10명까지만 반환
        if (out.size() == TOP_RECOMMENDATION_LIMIT) {
          break;
        }
      }
    }
    return out;
  }
}
