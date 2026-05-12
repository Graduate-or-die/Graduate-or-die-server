package server.pome.interviewQuestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.interviewQuestion.util.SimilarityUtils;
import server.pome.vector.infrastructure.embedding.EmbeddingClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionFilterService {

    private final EmbeddingClient embeddingClient;

    private double[] toDoubleArray(List<Float> vector) {
        double[] arr = new double[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            arr[i] = vector.get(i);
        }
        return arr;
    }

    public List<String> filterWithHistory(List<String> newQuestions, List<String> oldQuestions) {

        if (newQuestions == null || newQuestions.isEmpty()) {
            return new ArrayList<>();
        }

        // 기존 질문 임베딩
        List<double[]> oldEmbeddings = new ArrayList<>();

        if (oldQuestions != null && !oldQuestions.isEmpty()) {
            int limit = Math.min(5, oldQuestions.size());
            List<String> limitedOldQuestions = oldQuestions.subList(0, limit);
            oldEmbeddings = limitedOldQuestions.stream()
                    .map(q -> toDoubleArray(embeddingClient.embed(q)))
                    .toList();
        }

        // 새 질문 임베딩
        List<double[]> newEmbeddings = newQuestions.stream()
                .map(q -> toDoubleArray(embeddingClient.embed(q)))
                .toList();

        List<String> filteredQuestions = new ArrayList<>();
        List<double[]> filteredEmbeddings = new ArrayList<>();

        // 필터링
        for (int i = 0; i < newQuestions.size(); i++) {
            String currentQ = newQuestions.get(i);
            double[] currentEmb = newEmbeddings.get(i);

            boolean isDuplicate = false;

            // new vs old
            for (int j = 0; j < oldEmbeddings.size(); j++) {
                double sim = SimilarityUtils.cosineSimilarity(currentEmb, oldEmbeddings.get(j));

                if (sim >= 0.85) {
                    isDuplicate = true;
                    break;
                }
            }

            if (isDuplicate) continue;

            // new vs new
            for (double[] fEmb : filteredEmbeddings) {
                double sim = SimilarityUtils.cosineSimilarity(currentEmb, fEmb);

                if (sim >= 0.9) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                filteredQuestions.add(currentQ);
                filteredEmbeddings.add(currentEmb);
            }
        }

        return filteredQuestions;
    }
}
