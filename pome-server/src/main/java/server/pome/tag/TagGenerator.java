package server.pome.tag;

import java.util.List;
import server.pome.portfolio.dto.response.GetAllPortfolioResponse;

public interface TagGenerator {

  List<String> generateTags(GetAllPortfolioResponse portfolioList);
}
