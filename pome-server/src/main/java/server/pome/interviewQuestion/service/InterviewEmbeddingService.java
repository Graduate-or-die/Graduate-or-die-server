package server.pome.interviewQuestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.InterviewQuestion;
import server.pome.interviewQuestion.repository.InterviewQuestionRepository;
import server.pome.interviewQuestion.util.CanonicalTextBuilder;
import server.pome.vector.infrastructure.embedding.EmbeddingClient;
import server.pome.vector.infrastructure.vectorstore.VectorStoreNamespace;
import server.pome.vector.infrastructure.vectorstore.qdrant.QdrantVectorStoreClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InterviewEmbeddingService {
    private final InterviewQuestionRepository repository;
    private final EmbeddingClient embeddingClient;
    private final QdrantVectorStoreClient qdrantVectorStoreClient;

    @Transactional
    public void generateAndUpload() {

        List<InterviewQuestion> questions = repository.findAll();

        for (InterviewQuestion q : questions) {

            String canonical = CanonicalTextBuilder.build(q);

            List<Float> vector = embeddingClient.embed(canonical);

            Map<String, Object> payload = new HashMap<>();
            payload.put("topic", q.getTopic());
            payload.put("intent", q.getIntent());
            payload.put("role", q.getRole());
            payload.put("scope", q.getScope());
            payload.put("questionText", q.getQuestionText());

            qdrantVectorStoreClient.upsert(
                    VectorStoreNamespace.INTERVIEW_QUESTION,
                    q.getId(),
                    vector,
                    payload
            );
        }
    }
}
