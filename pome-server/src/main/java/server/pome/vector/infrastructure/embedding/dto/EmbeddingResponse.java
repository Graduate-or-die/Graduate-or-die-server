package server.pome.vector.infrastructure.embedding.dto;

public record EmbeddingResponse(
    float[] vector,
    int size
) {}
