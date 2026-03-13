package server.pome.vector.infrastructure.embedding;

import java.util.List;

public interface EmbeddingClient {

  List<Float> embed(String text);
}
