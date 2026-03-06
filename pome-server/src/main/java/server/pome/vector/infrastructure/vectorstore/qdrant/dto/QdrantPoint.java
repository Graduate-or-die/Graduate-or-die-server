package server.pome.vector.infrastructure.vectorstore.qdrant.dto;

import java.util.List;
import java.util.Map;

public record QdrantPoint(
    long id,
    List<Float> vector,
    Map<String, Object> payload
) {}
