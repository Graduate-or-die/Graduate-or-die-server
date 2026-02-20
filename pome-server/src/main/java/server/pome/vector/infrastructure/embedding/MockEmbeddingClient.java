package server.pome.vector.infrastructure.embedding;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class MockEmbeddingClient implements EmbeddingClient {

  private final Random random = new Random(0);

  @Override
  public List<Float> embed(String text) {
    return IntStream.range(0, 384)
        .mapToObj(i -> (float) (random.nextDouble() * 2 - 1))
        .toList();
  }
}
