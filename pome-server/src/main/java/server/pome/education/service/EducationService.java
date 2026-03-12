package server.pome.education.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.service.AttachmentService;
import server.pome.education.dto.request.SaveEducationRequest;
import server.pome.education.dto.request.UpdateEducationRequest;
import server.pome.education.dto.response.SaveUpdateEducationResponse;
import server.pome.education.repository.EducationRepository;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Education;
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
public class EducationService {

  private final EducationRepository educationRepository;
  private final PortfolioRepository portfolioRepository;
  private final UserRepository userRepository;
  private final AttachmentService attachmentService;
  private final PortfolioUpdateNotifier portfolioUpdateNotifier;

  // 학력 저장
  public SaveUpdateEducationResponse saveEducation(
      Long userId,
      SaveEducationRequest request,
      List<MultipartFile> files
  ) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

    if (!userRepository.existsById(userId)) {
      throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
    }

    if (educationRepository.findByPortfolio(portfolio).isPresent()) {
      throw new BaseException(BaseResponseStatus.DUPLICATE_EDUCATION);
    }

    Education education = request.toEntity(portfolio);
    educationRepository.save(education);

    attachmentService.uploadSingle(
        userId,
        portfolio.getId(),
        TypeEnum.EDUCATIONS,
        education.getId(),
        files
    );
    Optional<Attachment> attachment = attachmentService.findByPortfolioAndTypeAndBlock(
        portfolio.getId(),
        TypeEnum.EDUCATIONS,
        education.getId()
    );

    portfolioUpdateNotifier.notifyUpdated(userId);
    return SaveUpdateEducationResponse.from(education, attachment);
  }

  public SaveUpdateEducationResponse updateEducation(
      Long userId,
      Long educationId,
      UpdateEducationRequest request,
      List<MultipartFile> files
  ) {
    Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
    if (!userRepository.existsById(userId)) {
      throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
    }

    Education education = educationRepository.findByIdAndPortfolio_User_Id(educationId, userId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.EDUCATION_NOT_FOUND));

    String school = request.getSchool() != null && !request.getSchool().isEmpty()
        ? request.getSchool() : education.getSchool();
    String major = request.getMajor() != null && !request.getMajor().isEmpty()
        ? request.getMajor() : education.getMajor();
    String degree = request.getDegree() != null && !request.getDegree().isEmpty()
        ? request.getDegree() : education.getDegree();

    education.updateEducation(school, major, degree);

    attachmentService.replaceSingle(
        userId,
        portfolio.getId(),
        TypeEnum.EDUCATIONS,
        education.getId(),
        files
    );
    Optional<Attachment> attachment = attachmentService.findByPortfolioAndTypeAndBlock(
        portfolio.getId(),
        TypeEnum.EDUCATIONS,
        education.getId()
    );

    portfolioUpdateNotifier.notifyUpdated(userId);
    return SaveUpdateEducationResponse.from(education, attachment);
  }
}
