package server.pome.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.activity.dto.response.ActivitySectionResponse;
import server.pome.activity.dto.response.GetActivityListResponse;
import server.pome.activity.repository.ActivityRepository;
import server.pome.attachment.service.AttachmentService;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.service.PortfolioSectionQueryHandler;

@Component
@RequiredArgsConstructor
public class ActivitySectionQueryHandler implements PortfolioSectionQueryHandler {

  private final ActivityRepository activityRepository;
  private final AttachmentService attachmentService;

  @Override
  public TypeEnum supports() {
    return TypeEnum.ACTIVITIES;
  }

  @Override
  public Object query(Long portfolioId, Long userId) {
    var items = activityRepository.findAllByPortfolio_Id(portfolioId)
        .stream()
            .map(GetActivityListResponse::from)
        .toList();

    return new ActivitySectionResponse(items);
  }
}
