package server.pome.qualification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.service.AttachmentService;
import server.pome.global.domain.Portfolio;
import server.pome.global.domain.Qualification;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.qualification.dto.request.SaveQualificationRequest;
import server.pome.qualification.dto.request.UpdateQualificationRequest;
import server.pome.qualification.dto.response.SaveUpdateQualificationResponse;
import server.pome.qualification.repository.QualificationRepository;
import server.pome.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QualificationService {

    private final QualificationRepository qualificationRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;

    // 자격증 저장
    public SaveUpdateQualificationResponse saveQualification(Long userId, SaveQualificationRequest request, List<MultipartFile> files) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (request.getQualificationStartAt() == null && request.getQualificationEndAt() != null) {
            throw new BaseException(BaseResponseStatus.END_DATE_WITHOUT_START_DATE);
        }

        if (request.getQualificationStartAt() != null && request.getQualificationEndAt() != null) {
            if (request.getQualificationEndAt().isBefore(request.getQualificationStartAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        Qualification qualification = request.toEntity(portfolio);
        qualificationRepository.save(qualification);

        if (files != null && !files.isEmpty()) {
            TypeEnum type = TypeEnum.AWARDS;

            if (type.getS3Dir() == null) {
                throw new BaseException(BaseResponseStatus.FILE_NOT_SUPPORTED_TYPE);
            }

            attachmentService.uploadAll(
                    portfolio,
                    type.getId(),
                    qualification.getId(),
                    files
            );
        }

        return SaveUpdateQualificationResponse.from(qualification);
    }

    // 자격증 수정
    public SaveUpdateQualificationResponse updateQualification(Long userId, Long qualificationId, UpdateQualificationRequest request) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (request.getQualificationStartAt() == null && request.getQualificationEndAt() != null) {
            throw new BaseException(BaseResponseStatus.END_DATE_WITHOUT_START_DATE);
        }

        if (request.getQualificationStartAt() != null && request.getQualificationEndAt() != null) {
            if (request.getQualificationEndAt().isBefore(request.getQualificationStartAt())) {
                throw new BaseException(BaseResponseStatus.INVALID_DATE_RANGE);
            }
        }

        Qualification qualification = qualificationRepository.findByIdAndPortfolio_User_Id(qualificationId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.QUALIFICATION_NOT_FOUND));

        String qualificationName = request.getQualificationName() != null &&  !request.getQualificationName().isEmpty() ? request.getQualificationName() : qualification.getQualificationName();
        String qualificationOrganization = request.getQualificationOrganization() != null && !request.getQualificationOrganization().isEmpty() ? request.getQualificationOrganization() : qualification.getQualificationOrganization();
        LocalDate qualificationStartAt = request.getQualificationStartAt() != null ? request.getQualificationStartAt() : qualification.getQualificationStartAt();
        LocalDate qualificationEndAt = request.getQualificationEndAt() != null ? request.getQualificationEndAt() : qualification.getQualificationEndAt();
        boolean hasQualificationEndAt = request.isHasQualificationEndAt();
        int score = request.getScore();

        qualification.updateQualification(qualificationName, qualificationOrganization, qualificationStartAt, qualificationEndAt, hasQualificationEndAt, score);
        return SaveUpdateQualificationResponse.from(qualification);
    }
}
