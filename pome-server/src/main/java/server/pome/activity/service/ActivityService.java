package server.pome.activity.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Service;
import server.pome.activity.dto.request.SaveActivityRequest;
import server.pome.activity.dto.request.UpdateActivityRequest;
import server.pome.activity.dto.response.SaveActivityResponse;
import server.pome.activity.dto.response.UpdateActivityResponse;
import server.pome.activity.repository.ActivityRepository;
import server.pome.global.domain.Activity;
import server.pome.global.domain.Portfolio;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.user.repository.UserRepository;

import javax.sound.sampled.Port;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

        if (request.getActivityStartAt() == null && request.getActivityEndAt() != null) {
            throw new BaseException(BaseResponseStatus.END_DATE_WITHOUT_START_DATE);
        }

        if (request.getActivityStartAt() != null && request.getActivityEndAt() != null) {
            if (request.getActivityEndAt().isBefore(request.getActivityStartAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        Activity activity = request.toEntity(portfolio);
        activityRepository.save(activity);

        return SaveActivityResponse.from(activity);
    }

    // 대내외활동 수정
    public UpdateActivityResponse updateActivity(Long userId, Long activityId, UpdateActivityRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (request.getActivityStartAt() != null && request.getActivityEndAt() != null) {
            if (request.getActivityEndAt().isBefore(request.getActivityStartAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        Activity activity = activityRepository.findByIdAndPortfolio_User_Id(activityId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ACTIVITY_NOT_FOUND));

        String activityName = request.getActivityName() != null && !request.getActivityName().isEmpty() ? request.getActivityName() : activity.getActivityName();
        String activityRole = request.getActivityRole() != null && !request.getActivityRole().isEmpty() ? request.getActivityRole() : activity.getActivityRole();
        LocalDate activityStartAt = request.getActivityStartAt() != null ? request.getActivityStartAt() : activity.getActivityStartAt();
        LocalDate activityEndAt = request.getActivityEndAt() != null ? request.getActivityEndAt() : activity.getActivityEndAt();
        String result = request.getResult() != null && !request.getResult().isEmpty() ? request.getResult() : activity.getResult();

        activity.updateActivity(activityName, activityRole, activityStartAt, activityEndAt, result);
        return UpdateActivityResponse.from(activity);
    }
}
