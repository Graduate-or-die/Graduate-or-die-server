package server.pome.etc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.attachment.service.AttachmentService;
import server.pome.etc.dto.response.EtcSectionResponse;
import server.pome.etc.dto.response.GetEtcListResponse;
import server.pome.etc.repository.EtcRepository;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.service.PortfolioSectionQueryHandler;

@Component
@RequiredArgsConstructor
public class EtcSectionQueryHandler implements PortfolioSectionQueryHandler {

  private final EtcRepository etcRepository;
  private final AttachmentService attachmentService;

  @Override
  public TypeEnum supports() {
    return TypeEnum.ETCS;
  }

  @Override
  public Object query(Long portfolioId, Long userId) {
    var items = etcRepository.findAllByPortfolio_Id(portfolioId)
        .stream()
        .map(etc -> {
          var attachment = attachmentService.findByPortfolioAndTypeAndBlock(
              portfolioId,
              TypeEnum.ETCS,
              etc.getId()
          );

          return GetEtcListResponse.from(etc, attachment);
        })
        .toList();

    return new EtcSectionResponse(items);
  }
}
