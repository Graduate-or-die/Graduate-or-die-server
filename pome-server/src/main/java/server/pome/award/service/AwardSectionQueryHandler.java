package server.pome.award.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.attachment.service.AttachmentService;
import server.pome.award.dto.response.AwardSectionResponse;
import server.pome.award.dto.response.GetAwardListResponse;
import server.pome.award.repository.AwardRepository;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.service.PortfolioSectionQueryHandler;

@Component
@RequiredArgsConstructor
public class AwardSectionQueryHandler implements PortfolioSectionQueryHandler {

  private final AwardRepository awardRepository;
  private final AttachmentService attachmentService;

  @Override
  public TypeEnum supports() {
    return TypeEnum.AWARDS;
  }

  @Override
  public Object query(Long portfolioId, Long userId) {
    var items = awardRepository.findAllByPortfolio_Id(portfolioId)
        .stream()
            .map(award -> {
              var attachment = attachmentService.findByPortfolioAndTypeAndBlock(
                      portfolioId,
                      TypeEnum.AWARDS,
                      award.getId()
              );

              return GetAwardListResponse.from(award, attachment);
            })
        .toList();

    return new AwardSectionResponse(items);
  }
}
