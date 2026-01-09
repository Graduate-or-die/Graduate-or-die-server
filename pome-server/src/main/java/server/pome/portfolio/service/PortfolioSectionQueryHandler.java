package server.pome.portfolio.service;

import server.pome.global.enums.TypeEnum;

public interface PortfolioSectionQueryHandler {
  TypeEnum supports();

  Object query(Long portfolioId, Long userId);
}
