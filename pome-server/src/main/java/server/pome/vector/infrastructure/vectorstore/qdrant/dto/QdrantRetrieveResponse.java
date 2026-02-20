package server.pome.vector.infrastructure.vectorstore.qdrant.dto;

import java.util.List;

public record QdrantRetrieveResponse(
    List<Point> result
) {

  public record Point(
      long id,
      List<Float> vector
  ) {}
}
