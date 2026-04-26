package server.pome.interviewQuestion.util;

public class SimilarityUtils {

    public static double cosineSimilarity(double[] v1, double[] v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;

        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }

        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
