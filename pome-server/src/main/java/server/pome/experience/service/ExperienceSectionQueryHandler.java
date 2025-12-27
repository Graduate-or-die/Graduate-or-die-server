package server.pome.experience.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.experience.dto.response.ExperienceSectionResponse;
import server.pome.experience.dto.response.GetExperienceListResponse;
import server.pome.experience.repository.ExperienceRepository;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.service.PortfolioSectionQueryHandler;

@Component
@RequiredArgsConstructor
public class ExperienceSectionQueryHandler implements PortfolioSectionQueryHandler {

  private final ExperienceRepository experienceRepository;

  @Override
  public TypeEnum supports() {
    return TypeEnum.EXPERIENCES;
  }

  @Override
  public Object query(Long portfolioId, Long userId) {
    var items = experienceRepository.findAllByPortfolio_Id(portfolioId)
        .stream()
        .map(GetExperienceListResponse::from)
        .toList();

    return new ExperienceSectionResponse(items);
  }
}
