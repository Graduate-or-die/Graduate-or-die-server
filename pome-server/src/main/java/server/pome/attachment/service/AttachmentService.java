package server.pome.attachment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.dto.response.AttachmentResponse;
import server.pome.attachment.dto.response.UploadedFileInfo;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Portfolio;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;
import server.pome.attachment.repository.AttachmentRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final AwsS3Service awsS3Service; // 리뉴얼된 S3 서비스


    // 조회 (클라이언트 응답용)
    public AttachmentResponse getAttachment(
            Long userId,
            Long typeId,
            Long blockId
    ) {
        Portfolio portfolio = findPortfolio(userId);

        Attachment attachment = attachmentRepository.findByPortfolio_IdAndTypeIdAndBlockId(
                        portfolio.getId(), typeId, blockId)
                .orElse(null);

        if (attachment == null) {
            return null; // 파일 없는 경우 null 반환 가능
        }

        return AttachmentResponse.from(attachment);
    }

    // 파일 첨부
    public AttachmentResponse uploadFile(
            Long userId,
            Long typeId,
            Long blockId,
            MultipartFile file
    ) {
        Portfolio portfolio = findPortfolio(userId);


        Optional<Attachment> existing = attachmentRepository
                .findByPortfolio_IdAndTypeIdAndBlockId(portfolio.getId(), typeId, blockId);

        if (existing.isPresent()) {
            throw new BaseException(BaseResponseStatus.FILE_ALREADY_EXISTS);
        }

        // S3 업로드
        TypeEnum type = TypeEnum.fromId(typeId);
        if (type.getS3Dir() == null) {
            throw new BaseException(BaseResponseStatus.FILE_NOT_SUPPORTED_TYPE);
        }

        String dir = type.getS3Dir() + "/" + blockId;
        UploadedFileInfo info = awsS3Service.uploadFile(file, dir);

        // DB 저장
        Attachment attachment = Attachment.builder()
                .portfolio(portfolio)
                .typeId(typeId)
                .blockId(blockId)
                .originalFileName(file.getOriginalFilename())
                .storedFileName(info.getStoredFileName())
                .fileUrl(info.getFileUrl())
                .build();

        attachmentRepository.save(attachment);

        return AttachmentResponse.from(attachment);
    }


    // 파일 삭제 (블록 유지)
    public void deleteFile(
            Long userId,
            Long typeId,
            Long blockId
    ) {
        Portfolio portfolio = findPortfolio(userId);

        Attachment attachment = attachmentRepository
                .findByPortfolio_IdAndTypeIdAndBlockId(portfolio.getId(), typeId, blockId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.FILE_NOT_FOUND));

        // S3 삭제
        awsS3Service.deleteFile(attachment.getStoredFileName());

        // DB 삭제
        attachmentRepository.delete(attachment);
    }



    // 블록 삭제 시 파일 삭제 + DB 제거
    public void deleteByBlock(
            Long userId,
            Long typeId,
            Long blockId
    ) {
        // Portfolio 조회 (권한/소유 검사)
        Portfolio portfolio = findPortfolio(userId);

        // Attachment가 있으면 삭제
        Optional<Attachment> attachment = attachmentRepository
                .findByPortfolio_IdAndTypeIdAndBlockId(portfolio.getId(), typeId, blockId);

        attachment.ifPresent(att -> {
            awsS3Service.deleteFile(att.getStoredFileName());
            attachmentRepository.delete(att);
        });
    }

    // 유틸: 사용자 소유 포트폴리오 조회
    private Portfolio findPortfolio(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        if (portfolio == null) {
            throw new BaseException(BaseResponseStatus.PORTFOLIO_NOT_FOUND);
        }

        return portfolio;
    }
}