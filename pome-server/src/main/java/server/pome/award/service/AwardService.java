package server.pome.award.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.repository.AttachmentRepository;
import server.pome.attachment.service.AttachmentService;
import server.pome.award.dto.request.SaveAwardRequest;
import server.pome.award.dto.request.UpdateAwardRequest;
import server.pome.award.dto.response.SaveUpdateAwardResponse;
import server.pome.award.repository.AwardRepository;
import server.pome.global.domain.Activity;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Award;
import server.pome.global.domain.Portfolio;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AwardService {

    private final AwardRepository awardRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;

    // 수상경력 저장
    public SaveUpdateAwardResponse saveAward(Long userId, SaveAwardRequest request, MultipartFile file) {

        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        Award award = request.toEntity(portfolio);
        awardRepository.save(award);

        if (file != null && !file.isEmpty()) {
            attachmentService.uploadFile(
                    userId,
                    TypeEnum.AWARDS.getId(),
                    award.getId(),
                    file
            );
        }

        return SaveUpdateAwardResponse.from(award);
    }

    // 수상경력 수정
    public SaveUpdateAwardResponse updateAward(Long userId, Long awardId, UpdateAwardRequest request, MultipartFile file) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (!userRepository.existsById(userId)) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        Award award = awardRepository.findByIdAndPortfolio_User_Id(awardId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AWARD_NOT_FOUND));

        String awardName = request.getAwardName() != null &&  !request.getAwardName().isEmpty() ? request.getAwardName() : award.getAwardName();
        String awardOrganization = request.getAwardOrganization() != null && !request.getAwardOrganization().isEmpty() ? request.getAwardOrganization() : award.getAwardOrganization();
        LocalDate awardAt = request.getAwardAt() != null ? request.getAwardAt() : award.getAwardAt();
        String awardGrade = request.getAwardGrade() != null && !request.getAwardGrade().isEmpty() ? request.getAwardGrade() : award.getAwardGrade();

        award.updateAward(awardName, awardOrganization, awardAt, awardGrade);

        if (file != null && !file.isEmpty()) {
            Optional<Attachment> existing =
                    attachmentRepository.findByPortfolio_IdAndTypeIdAndBlockId(
                            award.getPortfolio().getId(),
                            TypeEnum.AWARDS.getId(),
                            awardId
                    );

            if (existing.isPresent()) {
                throw new BaseException(BaseResponseStatus.FILE_ALREADY_EXISTS);
            }

            attachmentService.uploadFile(userId, TypeEnum.AWARDS.getId(), awardId, file);
        }

        return SaveUpdateAwardResponse.from(award);
    }

    // 수상경력 삭제
    public void delete(Long blockId, Long userId) {
        Award award = awardRepository
                .findByIdAndPortfolio_User_Id(blockId, userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.PORTFOLIO_BLOCK_NOT_FOUND));
        awardRepository.delete(award);
    }
}
