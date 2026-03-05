package server.pome.vector.infrastructure.vectorstore.qdrant.dto;

import java.util.List;

public record UpsertRequest(
    List<QdrantPoint> points
) {}
