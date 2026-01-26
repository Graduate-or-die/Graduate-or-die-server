package server.pome.tag.OpenApiTagGenerator;

import java.util.List;

public interface OpenAiClient {

  List<String> generateTags(String normalizedText, int maxTags);
}
