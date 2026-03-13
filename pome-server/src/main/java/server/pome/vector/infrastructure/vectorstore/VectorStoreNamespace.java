package server.pome.vector.infrastructure.vectorstore;

public enum VectorStoreNamespace {
  MATCHING_USERS("user_vectors"),
  INTERVIEW_QUESTION("interview_vectors");

  private final String collection;

  VectorStoreNamespace(String collection) {
    this.collection = collection;
  }

  public String collection() {
    return collection;
  }
}
