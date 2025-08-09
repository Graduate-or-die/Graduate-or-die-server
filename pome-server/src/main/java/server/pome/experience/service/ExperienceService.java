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
import server.pome.user.repository.UserRepository;
import server.pome.user.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // 경력 저장
    public SaveExperienceResponse saveExperience(Long userId, SaveExperienceRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        Experience experience = request.toEntity(portfolio);
        experienceRepository.save(experience);

        return SaveExperienceResponse.from(experience);
    }
}
