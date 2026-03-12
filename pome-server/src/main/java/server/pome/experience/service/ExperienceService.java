package server.pome.experience.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.service.AttachmentService;
import server.pome.experience.dto.request.SaveExperienceRequest;
import server.pome.experience.dto.request.UpdateExperienceRequest;
import server.pome.experience.dto.response.SaveUpdateExperienceResponse;
import server.pome.experience.repository.ExperienceRepository;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Experience;
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
public class ExperienceService {

  private final ExperienceRepository experienceRepository;
  private final PortfolioRepository portfolioRepository;
  private final UserRepository userRepository;
  private final AttachmentService attachmentService;
  private final PortfolioUpdateNotifier portfolioUpdateNotifier;

  // 경력 저장
  public SaveUpdateExperienceResponse saveExperience(
      Long userId,
      SaveExperienceRequest request,
      List<MultipartFile> files
  ) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
    if (!userRepository.existsById(userId)) {
      throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
    }

    validateDateRange(request.getExperienceStartAt(), request.getExperienceEndAt());

    Experience experience = request.toEntity(portfolio);
    experienceRepository.save(experience);

    attachmentService.uploadSingle(
        userId,
        portfolio.getId(),
        TypeEnum.EXPERIENCES,
        experience.getId(),
        files
    );
    Optional<Attachment> attachment = attachmentService.findByPortfolioAndTypeAndBlock(
        portfolio.getId(),
        TypeEnum.EXPERIENCES,
        experience.getId()
    );

    portfolioUpdateNotifier.notifyUpdated(userId);
    return SaveUpdateExperienceResponse.from(experience, attachment);
  }

  // 경력 수정
  public SaveUpdateExperienceResponse updateExperience(
      Long userId,
      Long experienceId,
      UpdateExperienceRequest request,
      List<MultipartFile> files
  ) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
    if (!userRepository.existsById(userId)) {
      throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
    }

    Experience experience = experienceRepository.findByIdAndPortfolio_User_Id(experienceId, userId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.EXPERIENCE_NOT_FOUND));

    validateDateRange(request.getExperienceStartAt(), request.getExperienceEndAt());

    String workplace = request.getWorkplace() != null && !request.getWorkplace().isEmpty()
        ? request.getWorkplace() : experience.getWorkplace();
    String spot = request.getSpot() != null && !request.getSpot().isEmpty()
        ? request.getSpot() : experience.getSpot();
    LocalDate experienceStartAt = request.getExperienceStartAt() != null
        ? request.getExperienceStartAt() : experience.getExperienceStartAt();
    LocalDate experienceEndAt = request.getExperienceEndAt() != null
        ? request.getExperienceEndAt() : experience.getExperienceEndAt();

    experience.updateExperience(workplace, spot, experienceStartAt, experienceEndAt);

    attachmentService.replaceSingle(
        userId,
        portfolio.getId(),
        TypeEnum.EXPERIENCES,
        experience.getId(),
        files
    );
    Optional<Attachment> attachment = attachmentService.findByPortfolioAndTypeAndBlock(
        portfolio.getId(),
        TypeEnum.EXPERIENCES,
        experience.getId()
    );

    portfolioUpdateNotifier.notifyUpdated(userId);
    return SaveUpdateExperienceResponse.from(experience, attachment);
  }

  // 경력 삭제
  public void deleteExperience(Long blockId, Long userId) {
    Experience experience = experienceRepository
        .findByIdAndPortfolio_User_Id(blockId, userId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.PORTFOLIO_BLOCK_NOT_FOUND));

    attachmentService.deleteByBlock(userId, TypeEnum.EXPERIENCES, blockId);
    experienceRepository.delete(experience);
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
