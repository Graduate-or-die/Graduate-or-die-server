package server.pome.award.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.award.dto.response.AwardSectionResponse;
import server.pome.award.dto.response.GetAwardListResponse;
import server.pome.award.repository.AwardRepository;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.service.PortfolioSectionQueryHandler;

@Component
@RequiredArgsConstructor
public class AwardSectionQueryHandler implements PortfolioSectionQueryHandler {

  private final AwardRepository awardRepository;

  @Override
  public TypeEnum supports() {
    return TypeEnum.AWARDS;
  }

  @Override
  public Object query(Long portfolioId, Long userId) {
    var items = awardRepository.findAllByPortfolio_Id(portfolioId)
        .stream()
        .map(GetAwardListResponse::from)
        .toList();

    return new AwardSectionResponse(items);
  }
}
