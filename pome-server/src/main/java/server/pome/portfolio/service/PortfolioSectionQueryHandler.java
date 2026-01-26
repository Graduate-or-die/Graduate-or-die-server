package server.pome.portfolio.service;

import server.pome.global.enums.TypeEnum;

public interface PortfolioSectionQueryHandler {
  // 항목 반환
  TypeEnum supports();

  // user의 포트폴리오 중 supports()로 조회한 항목의 DTO 조회
  Object query(Long portfolioId, Long userId);
}
