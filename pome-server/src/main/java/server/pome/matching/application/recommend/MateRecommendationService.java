package server.pome.matching.application.recommend;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.matching.dto.response.RecommendCandidateDTO;
import server.pome.vector.domain.Vector;
import server.pome.vector.infrastructure.embedding.EmbeddingClient;
import server.pome.vector.infrastructure.vectorstore.VectorStoreClient;
import server.pome.vector.infrastructure.vectorstore.VectorStoreNamespace;
import server.pome.vector.infrastructure.vectorstore.model.SearchResult;

@Service
@RequiredArgsConstructor
public class MateRecommendationService {

  private final VectorStoreClient vectorStoreClient;
  private final CandidateFilterPort candidateFilterPort;

  public List<RecommendCandidateDTO> recommend(long me) {
    // 자신의 벡터를 Qdrant에서 불러옴
    var myVector = vectorStoreClient.getVector(VectorStoreNamespace.MATCHING_USERS, me);

    // 벡터가 없으면 추천 안함 (정책에 따라 수정 필요)
    if (myVector.isEmpty()) {
      return List.of();
    }

    // 불러온 벡터 유효성 검증
    Vector vector = new Vector(myVector.get()).validateDimension(
        VectorStoreNamespace.MATCHING_USERS);

    // topK 후보 검색
    List<SearchResult> hits = vectorStoreClient.search(
        VectorStoreNamespace.MATCHING_USERS,
        vector.asList(),
        100
    );

    List<Long> ids = hits.stream()
        .map(SearchResult::id).toList();

    // candidateFilter 적용 후 상위 10명 반환
    Set<Long> eligible = candidateFilterPort.filterEligible(me, ids);

    List<RecommendCandidateDTO> out = new ArrayList<>();
    for (SearchResult h : hits) {
      if (eligible.contains(h.id())) {
        out.add(new RecommendCandidateDTO(h.id(), h.score()));
        if (out.size() == 10) {
          break;
        }
      }
    }
    return out;
  }
}
