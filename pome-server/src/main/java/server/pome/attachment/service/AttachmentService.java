package server.pome.attachment.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.dto.response.FileResponse;
import server.pome.attachment.dto.response.UploadedFileInfo;
import server.pome.attachment.repository.AttachmentRepository;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Portfolio;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.repository.PortfolioRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final AwsS3Service awsS3Service;

    // 첨부 파일 조회
    public FileResponse getAttachment(Long userId, Long typeId, Long blockId) {
        return findAttachmentByUser(userId, typeId, blockId)
                .map(FileResponse::from)
                .orElse(null);
    }

    public Optional<Attachment> findByPortfolioAndTypeAndBlock(Long portfolioId, TypeEnum type, Long blockId) {
        return findAttachment(portfolioId, type.getId(), blockId);
    }

    // 파일 1개 업로드
    public void uploadSingle(
            Long userId,
            Long portfolioId,
            TypeEnum type,
            Long blockId,
            List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            return;
        }

        if (files.size() > 1) {
            throw new BaseException(BaseResponseStatus.FILE_LIMIT_EXCEEDED);
        }

        if (findByPortfolioAndTypeAndBlock(portfolioId, type, blockId).isPresent()) {
            throw new BaseException(BaseResponseStatus.FILE_ALREADY_EXISTS);
        }

        uploadFile(userId, type.getId(), blockId, files.get(0));
    }

    // 파일 업로드
    public FileResponse uploadFile(Long userId, Long typeId, Long blockId, MultipartFile file) {
        Portfolio portfolio = findPortfolio(userId);

        if (findAttachment(portfolio.getId(), typeId, blockId).isPresent()) {
            throw new BaseException(BaseResponseStatus.FILE_ALREADY_EXISTS);
        }

        // 타입별 업로드 가능 여부 확인
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
                .S3ObjectKey(info.getStoredKey())
                .build();

        attachmentRepository.save(attachment);
        return FileResponse.from(attachment);
    }

    // 파일 삭제
    public void deleteFile(Long userId, Long typeId, Long blockId) {
        Attachment attachment = findAttachmentByUser(userId, typeId, blockId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.FILE_NOT_FOUND));

        deleteAttachment(attachment);
    }

    // 블록 삭제 시 첨부가 있으면 함께 삭제
    public void deleteByBlock(Long userId, Long typeId, Long blockId) {
        findAttachmentByUser(userId, typeId, blockId)
                .ifPresent(this::deleteAttachment);
    }

    public void deleteByBlock(Long userId, TypeEnum type, Long blockId) {
        deleteByBlock(userId, type.getId(), blockId);
    }

    // 사용자의 포트폴리오 조회
    private Portfolio findPortfolio(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);
        if (portfolio == null) {
            throw new BaseException(BaseResponseStatus.PORTFOLIO_NOT_FOUND);
        }
        return portfolio;
    }

    private Optional<Attachment> findAttachmentByUser(Long userId, Long typeId, Long blockId) {
        Portfolio portfolio = findPortfolio(userId);
        return findAttachment(portfolio.getId(), typeId, blockId);
    }

    private Optional<Attachment> findAttachment(Long portfolioId, Long typeId, Long blockId) {
        return attachmentRepository.findByPortfolio_IdAndTypeIdAndBlockId(portfolioId, typeId, blockId);
    }

    private void deleteAttachment(Attachment attachment) {
        awsS3Service.deleteFile(attachment.getS3ObjectKey());
        attachmentRepository.delete(attachment);
    }
}
