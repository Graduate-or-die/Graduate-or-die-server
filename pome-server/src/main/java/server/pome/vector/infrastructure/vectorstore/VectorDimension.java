package server.pome.vector.infrastructure.vectorstore;

import static server.pome.vector.infrastructure.vectorstore.VectorStoreNamespace.INTERVIEW_QUESTION;

public final class VectorDimension {
  private VectorDimension() {}

  public static final int MATCHING_USERS = 384;
  public static final int INTERVIEW_QUESTION = 384;

  public static int expectedFor(VectorStoreNamespace ns) {
    return switch (ns) {
      case MATCHING_USERS -> MATCHING_USERS;
      case INTERVIEW_QUESTION -> INTERVIEW_QUESTION;
    };
  }
}
