package server.pome.matching.application.indexing;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.matching.domain.UserVectorState;
import server.pome.matching.infrastructure.repository.UserVectorStateRepository;
import server.pome.vector.domain.Vector;
import server.pome.vector.infrastructure.embedding.EmbeddingClient;
import server.pome.vector.infrastructure.vectorstore.VectorStoreClient;
import server.pome.vector.infrastructure.vectorstore.VectorStoreNamespace;

@Service
@RequiredArgsConstructor
public class UserVectorIndexingService {

  private final UserVectorStateRepository userVectorStateRepository;
  private final UserEmbeddingTextPort textPort;
  private final EmbeddingClient embeddingClient;
  private final VectorStoreClient vectorStoreClient;

  @Transactional
  public void indexOne(UserVectorState state) {
    long userId = state.getUserId();
    try {
      // 유저 임베딩 텍스트 생성
      String text = textPort.getUserEmbeddingText(userId, state.getEmbeddingVersion());

      // 임베딩 서버 호출
      List<Float> raw = embeddingClient.embed(text);
      Vector vector = new Vector(raw).validateDimension(VectorStoreNamespace.MATCHING_USERS);

      // Qdrant 업서트
      vectorStoreClient.upsert(
          VectorStoreNamespace.MATCHING_USERS,
          userId,
          vector.asList(),
          Map.of("userId", userId, "version", state.getEmbeddingVersion())
      );

      // 상태 READY 처리
      state.markReady();
      userVectorStateRepository.save(state);
    } catch (Exception e) {
      state.markFailed(e.getMessage());
      userVectorStateRepository.save(state);
    }
  }
}
