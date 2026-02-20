package server.pome.vector.infrastructure.vectorstore.qdrant.dto;

import java.util.List;

public record SearchRequest(
    List<Float> vector,
    int limit,
    boolean with_payload,
    boolean with_vector
) {}
