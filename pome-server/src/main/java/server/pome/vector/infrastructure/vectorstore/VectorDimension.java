package server.pome.vector.infrastructure.vectorstore;

public final class VectorDimension {
  private VectorDimension() {}

  public static final int MATCHING_USERS = 384;
  // public static final int INTERVIEW = 384;

  public static int expectedFor(VectorStoreNamespace ns) {
    return switch (ns) {
      case MATCHING_USERS -> MATCHING_USERS;
      // case INTERVEW -> INTERVIEW;
    };
  }
}
