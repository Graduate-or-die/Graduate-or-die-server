package server.pome.attachment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.dto.response.AttachmentResponse;
import server.pome.attachment.repository.AttachmentRepository;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.Portfolio;
import server.pome.global.enums.TypeEnum;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.global.infra.s3.S3Uploader;
import server.pome.global.infra.s3.UploadFileResult;
import server.pome.portfolio.repository.PortfolioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final S3Uploader s3Uploader;

    public void uploadAll(
            Portfolio portfolio,
            Long typeId,
            Long blockId,
            List<MultipartFile> files
    ) {
        TypeEnum type = TypeEnum.fromId(typeId);

        if (type.getS3Dir() == null) {
            throw new BaseException(BaseResponseStatus.FILE_NOT_SUPPORTED_TYPE);
        }

        for (MultipartFile file : files) {
            UploadFileResult result = s3Uploader.upload(file, type.getS3Dir());

            Attachment attachment = Attachment.builder()
                    .portfolio(portfolio)
                    .typeId(typeId)
                    .blockId(blockId)
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(result.getStoredFileName())
                    .fileUrl(result.getFileUrl())
                    .build();

            attachmentRepository.save(attachment);
        }
    }

    public List<AttachmentResponse> getAttachments(
            Long userId,
            Long typeId,
            Long blockId
    ) {
        Portfolio portfolio = portfolioRepository.findByUser_Id(userId);

        List<Attachment> attachments =
                attachmentRepository.findByPortfolio_IdAndTypeIdAndBlockId(
                        portfolio.getId(),
                        typeId,
                        blockId
                );

        return attachments.stream()
                .map(AttachmentResponse::from)
                .toList();
    }
}
