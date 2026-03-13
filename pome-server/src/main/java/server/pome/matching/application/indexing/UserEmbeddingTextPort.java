package server.pome.matching.application.indexing;

public interface UserEmbeddingTextPort {

  String getUserEmbeddingText(long userId, String embeddingVersion);
}
