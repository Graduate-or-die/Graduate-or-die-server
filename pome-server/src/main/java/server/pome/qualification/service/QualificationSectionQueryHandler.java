package server.pome.qualification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.attachment.service.AttachmentService;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.service.PortfolioSectionQueryHandler;
import server.pome.qualification.dto.response.GetQualificationListResponse;
import server.pome.qualification.dto.response.QualificationSectionResponse;
import server.pome.qualification.repository.QualificationRepository;

@Component
@RequiredArgsConstructor
public class QualificationSectionQueryHandler implements PortfolioSectionQueryHandler {

  private final QualificationRepository qualificationRepository;
  private final AttachmentService attachmentService;

  @Override
  public TypeEnum supports() {
    return TypeEnum.QUALIFICATIONS;
  }

  @Override
  public Object query(Long portfolioId, Long userId) {
    var items = qualificationRepository.findAllByPortfolio_Id(portfolioId)
        .stream()
            .map(qualification -> {
              var attachment = attachmentService.findByPortfolioAndTypeAndBlock(
                      portfolioId,
                      TypeEnum.QUALIFICATIONS,
                      qualification.getId()
              );

              return GetQualificationListResponse.from(qualification, attachment);
            })
            .toList();

    return new QualificationSectionResponse(items);
  }
}
