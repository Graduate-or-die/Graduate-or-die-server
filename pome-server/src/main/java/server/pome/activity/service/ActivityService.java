package server.pome.activity.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.pome.activity.dto.request.SaveActivityRequest;
import server.pome.activity.dto.request.UpdateActivityRequest;
import server.pome.activity.dto.response.SaveUpdateActivityResponse;
import server.pome.activity.repository.ActivityRepository;
import server.pome.attachment.service.AttachmentService;
import server.pome.global.domain.Activity;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Portfolio;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.portfolio.service.event.PortfolioUpdateNotifier;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

  private final ActivityRepository activityRepository;
  private final PortfolioRepository portfolioRepository;
  private final UserRepository userRepository;
  private final AttachmentService attachmentService;
  private final PortfolioUpdateNotifier portfolioUpdateNotifier;

  // 대내외활동 저장
  public SaveUpdateActivityResponse saveActivity(
      Long userId,
      SaveActivityRequest request,
      List<MultipartFile> files
  ) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
    if (!userRepository.existsById(userId)) {
      throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
    }

    validateDateRange(request.getActivityStartAt(), request.getActivityEndAt());

    Activity activity = request.toEntity(portfolio);
    activityRepository.save(activity);

    attachmentService.uploadSingle(
        userId,
        portfolio.getId(),
        TypeEnum.ACTIVITIES,
        activity.getId(),
        files
    );
    Optional<Attachment> attachment = attachmentService.findByPortfolioAndTypeAndBlock(
        portfolio.getId(),
        TypeEnum.ACTIVITIES,
        activity.getId()
    );

    portfolioUpdateNotifier.notifyUpdated(userId);
    return SaveUpdateActivityResponse.from(activity, attachment);
  }

  // 대내외활동 수정
  public SaveUpdateActivityResponse updateActivity(
      Long userId,
      Long activityId,
      UpdateActivityRequest request,
      List<MultipartFile> files
  ) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
    if (!userRepository.existsById(userId)) {
      throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
    }

    validateDateRange(request.getActivityStartAt(), request.getActivityEndAt());

    Activity activity = activityRepository.findByIdAndPortfolio_User_Id(activityId, userId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.ACTIVITY_NOT_FOUND));

    String activityName = request.getActivityName() != null && !request.getActivityName().isEmpty()
        ? request.getActivityName() : activity.getActivityName();
    String activityRole = request.getActivityRole() != null && !request.getActivityRole().isEmpty()
        ? request.getActivityRole() : activity.getActivityRole();
    LocalDate activityStartAt = request.getActivityStartAt() != null
        ? request.getActivityStartAt() : activity.getActivityStartAt();
    LocalDate activityEndAt = request.getActivityEndAt() != null
        ? request.getActivityEndAt() : activity.getActivityEndAt();
    String result = request.getResult() != null && !request.getResult().isEmpty()
        ? request.getResult() : activity.getResult();

    activity.updateActivity(activityName, activityRole, activityStartAt, activityEndAt, result);

    attachmentService.replaceSingle(
        userId,
        portfolio.getId(),
        TypeEnum.ACTIVITIES,
        activity.getId(),
        files
    );
    Optional<Attachment> attachment = attachmentService.findByPortfolioAndTypeAndBlock(
        portfolio.getId(),
        TypeEnum.ACTIVITIES,
        activity.getId()
    );

    portfolioUpdateNotifier.notifyUpdated(userId);
    return SaveUpdateActivityResponse.from(activity, attachment);
  }

  public void delete(Long blockId, Long userId) {
    Activity activity = activityRepository
        .findByIdAndPortfolio_User_Id(blockId, userId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.PORTFOLIO_BLOCK_NOT_FOUND));

    attachmentService.deleteByBlock(userId, TypeEnum.ACTIVITIES, blockId);
    activityRepository.delete(activity);
  }

  private void validateDateRange(LocalDate startAt, LocalDate endAt) {
    if (startAt == null && endAt != null) {
      throw new BaseException(BaseResponseStatus.END_DATE_WITHOUT_START_DATE);
    }

    if (startAt != null && endAt != null && endAt.isBefore(startAt)) {
      throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
    }
  }
}
