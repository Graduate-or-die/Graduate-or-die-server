package server.pome.file.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.attachment.repository.AttachmentRepository;
import server.pome.global.domain.Attachment;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.global.infra.s3.AmazonS3ObjectStorageClient;
import server.pome.global.infra.s3.StoredObject;
import server.pome.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final AmazonS3ObjectStorageClient objectStorageClient;

    public FileDownload downloadProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        String key = user.getProfileImage();
        if (key == null || key.isBlank()) {
            throw new BaseException(BaseResponseStatus.FILE_RESOURCE_NOT_FOUND);
        }

        StoredObject storedObject = objectStorageClient.getObject(key);
        String contentType = resolveContentType(storedObject.getContentType(), null, key);

        return new FileDownload(
                storedObject.getInputStream(),
                storedObject.getContentLength(),
                contentType,
                null,
                true
        );
    }

    public FileDownload downloadPortfolioAttachment(Long userId, Long attachmentId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.FILE_RESOURCE_NOT_FOUND));

        String key = attachment.getS3ObjectKey();
        if (key == null || key.isBlank()) {
            throw new BaseException(BaseResponseStatus.FILE_RESOURCE_NOT_FOUND);
        }

        StoredObject storedObject = objectStorageClient.getObject(key);
        String contentType = resolveContentType(
                storedObject.getContentType(),
                attachment.getOriginalFileName(),
                key
        );

        return new FileDownload(
                storedObject.getInputStream(),
                storedObject.getContentLength(),
                contentType,
                attachment.getOriginalFileName(),
                false
        );
    }

    private String resolveContentType(String metadataContentType, String originalFileName, String key) {
        if (metadataContentType != null && !metadataContentType.isBlank()) {
            return metadataContentType;
        }

        String candidateName = Optional.ofNullable(originalFileName)
                .filter(name -> !name.isBlank())
                .orElseGet(() -> extractFileName(key));

        return MediaTypeFactory.getMediaType(candidateName)
                .map(MediaType::toString)
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    private String extractFileName(String key) {
        if (key == null) {
            return "file";
        }
        int lastSlash = key.lastIndexOf('/');
        return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
    }
}
