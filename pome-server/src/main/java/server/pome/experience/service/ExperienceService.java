package server.pome.experience.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.experience.dto.request.SaveExperienceRequest;
import server.pome.experience.dto.request.UpdateExperienceRequest;
import server.pome.experience.dto.response.SaveUpdateExperienceResponse;
import server.pome.experience.repository.ExperienceRepository;
import server.pome.global.domain.Experience;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.portfolio.service.event.PortfolioUpdateNotifier;
import server.pome.user.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    private final PortfolioUpdateNotifier portfolioUpdateNotifier;

    // 경력 저장
    public SaveUpdateExperienceResponse saveExperience(Long userId, SaveExperienceRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (request.getExperienceStartAt() == null && request.getExperienceEndAt() != null) {
            throw new BaseException(BaseResponseStatus.END_DATE_WITHOUT_START_DATE);
        }

        if (request.getExperienceStartAt() != null && request.getExperienceEndAt() != null) {
            if (request.getExperienceEndAt().isBefore(request.getExperienceStartAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        Experience experience = request.toEntity(portfolio);
        experienceRepository.save(experience);

        portfolioUpdateNotifier.notifyUpdated(userId);

        return SaveUpdateExperienceResponse.from(experience);
    }

    // 경력 수정
    public SaveUpdateExperienceResponse updateExperience(Long userId, Long experienceId, UpdateExperienceRequest request){
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        Experience experience = experienceRepository.findByIdAndPortfolio_User_Id(experienceId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.EXPERIENCE_NOT_FOUND));

        if (request.getExperienceStartAt() != null && request.getExperienceEndAt() != null) {
            if (request.getExperienceEndAt().isBefore(request.getExperienceStartAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        String workplace = request.getWorkplace() != null && !request.getWorkplace().isEmpty() ? request.getWorkplace() : experience.getWorkplace();
        String spot = request.getSpot() != null && !request.getSpot().isEmpty() ? request.getSpot() : experience.getSpot();
        LocalDate experienceStartAt = request.getExperienceStartAt() != null ? request.getExperienceStartAt() : experience.getExperienceStartAt();
        LocalDate experienceEndAt = request.getExperienceEndAt() != null ? request.getExperienceEndAt() : experience.getExperienceEndAt();

        experience.updateExperience(workplace, spot, experienceStartAt, experienceEndAt);

        portfolioUpdateNotifier.notifyUpdated(userId);

        return SaveUpdateExperienceResponse.from(experience);
    }

    // 경력 삭제
    public void delete(Long blockId, Long userId) {
        Experience experience = experienceRepository
                .findByIdAndPortfolio_User_Id(blockId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.PORTFOLIO_BLOCK_NOT_FOUND));

        experienceRepository.delete(experience);
    }
}
