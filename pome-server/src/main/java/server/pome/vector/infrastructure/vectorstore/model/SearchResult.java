package server.pome.vector.infrastructure.vectorstore.model;

public record SearchResult(
    long id,
    double score
) {}
