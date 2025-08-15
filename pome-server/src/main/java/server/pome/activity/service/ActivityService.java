package server.pome.activity.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.pome.activity.dto.request.SaveActivityRequest;
import server.pome.activity.dto.response.SaveActivityResponse;
import server.pome.activity.repository.ActivityRepository;
import server.pome.global.domain.Activity;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.user.repository.UserRepository;

import javax.sound.sampled.Port;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    // 대내외활동 저장
    public SaveActivityResponse saveActivity(Long userId, SaveActivityRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (request.getActivityStartAt() != null && request.getActivityEndAt() != null) {
            if (request.getActivityStartAt().isBefore(request.getActivityEndAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        Activity activity = request.toEntity(portfolio);
        activityRepository.save(activity);

        return SaveActivityResponse.from(activity);
    }
}
