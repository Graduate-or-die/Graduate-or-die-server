package server.pome.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.Project;
import server.pome.global.domain.Qualification;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.project.dto.request.SaveProjectRequest;
import server.pome.project.dto.request.UpdateProjectRequest;
import server.pome.project.dto.response.SaveUpdateProjectResponse;
import server.pome.project.repository.ProjectRepository;
import server.pome.user.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // 프로젝트 저장
    public SaveUpdateProjectResponse saveProject(Long userId, SaveProjectRequest request) {
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

        return SaveUpdateProjectResponse.from(project);
    }

    // 프로젝트 수정
    public SaveUpdateProjectResponse updateProject(Long userId, Long projectId, UpdateProjectRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (request.getProjectStartAt() != null && request.getProjectEndAt() != null) {
            if (request.getProjectEndAt().isBefore(request.getProjectStartAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        Project project = projectRepository.findByIdAndPortfolio_User_Id(projectId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.PROJECT_NOT_FOUND));

        String projectName = request.getProjectName() != null &&  !request.getProjectName().isEmpty() ? request.getProjectName() : project.getProjectName();
        LocalDate projectStartAt = request.getProjectStartAt() != null ? request.getProjectStartAt() : project.getProjectStartAt();
        LocalDate projectEndAt = request.getProjectEndAt() != null ? request.getProjectEndAt() : project.getProjectEndAt();
        String projectRole = request.getProjectRole() != null &&  !request.getProjectRole().isEmpty() ? request.getProjectRole() : project.getProjectRole();
        String projectDescription = request.getProjectDescription() != null &&  !request.getProjectDescription().isEmpty() ? request.getProjectDescription() : project.getProjectDescription();
        String projectAward = request.getProjectAward() != null &&  !request.getProjectAward().isEmpty() ? request.getProjectAward() : project.getProjectAward();

        project.UpdateProject(projectName, projectStartAt, projectEndAt, projectRole, projectDescription, projectAward);
        return SaveUpdateProjectResponse.from(project);


    }

    // 프로젝트 삭제
    public void delete(Long blockId, Long userId) {
        Project project = projectRepository
                .findByIdAndPortfolio_User_Id(blockId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.PORTFOLIO_BLOCK_NOT_FOUND));
        projectRepository.delete(project);
    }
}
