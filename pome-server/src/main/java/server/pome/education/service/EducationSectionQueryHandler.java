package server.pome.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.attachment.service.AttachmentService;
import server.pome.education.dto.response.EducationSectionResponse;
import server.pome.education.dto.response.GetEducationListResponse;
import server.pome.education.repository.EducationRepository;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.service.PortfolioSectionQueryHandler;

@Component
@RequiredArgsConstructor
public class EducationSectionQueryHandler implements PortfolioSectionQueryHandler {

  private final EducationRepository educationRepository;
  @Override
  public TypeEnum supports() {
    return TypeEnum.EDUCATIONS;
  }

  @Override
  public Object query(Long portfolioId, Long userId) {
    var items = educationRepository.findAllByPortfolio_Id(portfolioId)
        .stream()
            .map(GetEducationListResponse::from)
        .toList();

    return new EducationSectionResponse(items);
  }
}
