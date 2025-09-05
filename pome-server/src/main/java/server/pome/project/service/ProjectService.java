package server.pome.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.Project;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.project.dto.request.SaveProjectRequest;
import server.pome.project.dto.response.SaveProjectResponse;
import server.pome.project.repository.ProjectRepository;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // 프로젝트 저장
    public SaveProjectResponse saveProject(Long userId, SaveProjectRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (request.getProjectStartAt() == null && request.getProjectEndAt() != null) {
            throw new BaseException(BaseResponseStatus.END_DATE_WITHOUT_START_DATE);
        }

        if (request.getProjectStartAt() != null && request.getProjectEndAt() != null) {
            if (request.getProjectEndAt().isBefore(request.getProjectStartAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        Project project = request.toEntity(portfolio);
        projectRepository.save(project);

        return SaveProjectResponse.from(project);
    }
}
