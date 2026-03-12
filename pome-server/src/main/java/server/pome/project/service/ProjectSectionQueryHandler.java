package server.pome.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.attachment.service.AttachmentService;
import server.pome.global.enums.TypeEnum;
import server.pome.portfolio.service.PortfolioSectionQueryHandler;
import server.pome.project.dto.response.GetProjectListResponse;
import server.pome.project.dto.response.ProjectSectionResponse;
import server.pome.project.repository.ProjectRepository;

@Component
@RequiredArgsConstructor
public class ProjectSectionQueryHandler implements PortfolioSectionQueryHandler {

  private final ProjectRepository projectRepository;
  private final AttachmentService attachmentService;

  @Override
  public TypeEnum supports() {
    return TypeEnum.PROJECTS;
  }

  @Override
  public Object query(Long portfolioId, Long userId) {
    var items = projectRepository.findAllByPortfolio_Id(portfolioId)
        .stream()
        .map(project -> {
          var attachment = attachmentService.findByPortfolioAndTypeAndBlock(
              portfolioId,
              TypeEnum.PROJECTS,
              project.getId()
          );

          return GetProjectListResponse.from(project, attachment);
        })
        .toList();

    return new ProjectSectionResponse(items);
  }
}
