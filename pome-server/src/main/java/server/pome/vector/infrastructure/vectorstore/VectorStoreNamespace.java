package server.pome.vector.infrastructure.vectorstore;

public enum VectorStoreNamespace {
  MATCHING_USERS("user_vectors");
  // INTERVIEW("interview_vectors")

  private final String collection;

  VectorStoreNamespace(String collection) {
    this.collection = collection;
  }

  public String collection() {
    return collection;
  }
}
