package server.pome.tag.OpenApiTagGenerator;

import server.pome.portfolio.dto.response.GetAllPortfolioResponse;

public interface PortfolioTagNormalizer {

  String normalize(GetAllPortfolioResponse portfolioList);
}
