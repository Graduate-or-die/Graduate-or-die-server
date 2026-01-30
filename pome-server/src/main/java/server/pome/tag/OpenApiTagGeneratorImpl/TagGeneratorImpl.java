package server.pome.tag.OpenApiTagGeneratorImpl;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.tag.OpenApiTagGenerator.OpenAiClient;
import server.pome.tag.OpenApiTagGenerator.PortfolioTagNormalizer;
import server.pome.tag.OpenApiTagGenerator.TagPostProcessor;
import server.pome.tag.TagGenerator;
import server.pome.portfolio.dto.response.GetAllPortfolioResponse;

@Service
@AllArgsConstructor
public class TagGeneratorImpl implements TagGenerator {

  private final PortfolioTagNormalizer portfolioTagNormalizer;
  private final OpenAiClient openAiClient;
  private final TagPostProcessor tagPostProcessor;

  private static final int MAX_TAGS = 6;

  @Override
  public List<String> generateTags(GetAllPortfolioResponse portfolio) {
    String input = portfolioTagNormalizer.normalize(portfolio);
    List<String> raw = openAiClient.generateTags(input, MAX_TAGS);
    return tagPostProcessor.process(raw);
  }
}
