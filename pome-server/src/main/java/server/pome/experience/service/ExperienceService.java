package server.pome.experience.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.experience.dto.request.SaveExperienceRequest;
import server.pome.experience.dto.response.SaveExperienceResponse;
import server.pome.experience.repository.ExperienceRepository;
import server.pome.global.domain.Experience;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final PortfolioRepository portfolioRepository;

    // 경력 저장
    public SaveExperienceResponse saveExperience(Long userId, SaveExperienceRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (portfolio == null) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }
        Experience experience = new Experience(
                null,
                portfolio,
                request.getWorkplace(),
                request.getSpot(),
                request.getExperienceStartAt(),
                request.getExperienceEndAt()
        );
        experienceRepository.save(experience);
        return SaveExperienceResponse.builder()
                .experienceId(experience.getId())
                .workplace(experience.getWorkplace())
                .spot(experience.getSpot())
                .experienceStartAt(experience.getExperienceStartAt())
                .experienceEndAt(experience.getExperienceEndAt())
                .build();
    }
}
