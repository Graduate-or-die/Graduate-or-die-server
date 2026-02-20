package server.pome.vector.infrastructure.vectorstore;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import server.pome.vector.infrastructure.vectorstore.model.SearchResult;

public interface VectorStoreClient {

  void upsert(VectorStoreNamespace ns, long pointId, List<Float> vector,
      Map<String, Object> payload);

  List<SearchResult> search(VectorStoreNamespace ns, List<Float> vector, int limit);

  Optional<List<Float>> getVector(VectorStoreNamespace ns, long pointId);
}
