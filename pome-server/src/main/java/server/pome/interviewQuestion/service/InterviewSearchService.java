package server.pome.interviewQuestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.interviewQuestion.repository.InterviewQuestionRepository;
import server.pome.mate.service.MateQueryService;
import server.pome.vector.infrastructure.embedding.EmbeddingClient;
import server.pome.vector.infrastructure.vectorstore.VectorStoreNamespace;
import server.pome.vector.infrastructure.vectorstore.qdrant.QdrantVectorStoreClient;
import server.pome.vector.infrastructure.vectorstore.qdrant.dto.SearchHit;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewSearchService {

    // Vector Search에서 가져올 유사 질문 개수 (LLM이 참고할 질문 후보군)
    private static final int TOP_K = 8;
    private final QdrantVectorStoreClient qdrantVectorStoreClient;
    private final InterviewQuestionRepository interviewQuestionrepository;
    private final EmbeddingClient embeddingClient;
    private final MateQueryService mateQueryService;

    public String buildPortfolio(Long userId) {

        StringBuilder portfolioBuilder = new StringBuilder();

        for (long typeId = 1; typeId <=6; typeId++) {

            Object section =
                    mateQueryService.getMatePortfolio(userId, typeId);

            if (section != null) {
                portfolioBuilder.append(section.toString()).append("\n");
            }
        }
        return portfolioBuilder.toString();
    }

    // 포트폴리오 기반 유사 면접 질문 검색
    public List<String> findSimilarQuestions(String portfolio) {

        // 포폴 텍스트를 임베딩 Vector로 변환
        List<Float> vector = embeddingClient.embed(portfolio);

        // Qdrant에서 유사 질문 Vector 검색
        List<SearchHit> hits = qdrantVectorStoreClient.search(
                VectorStoreNamespace.INTERVIEW_QUESTION,
                vector,
                TOP_K
        );

        return hits.stream()
                .map(hit -> interviewQuestionrepository.findById(Long.valueOf(hit.id()))
                        .orElseThrow()
                        .getQuestionText())
                .toList();
    }
}
