package server.pome.vector.infrastructure.vectorstore.qdrant.dto;

public record SearchHit(
    long id,
    double score
) {}
