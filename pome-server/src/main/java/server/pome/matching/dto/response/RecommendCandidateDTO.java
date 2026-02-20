package server.pome.matching.dto.response;

public record RecommendCandidateDTO(
    long userId,
    double score
) {}
