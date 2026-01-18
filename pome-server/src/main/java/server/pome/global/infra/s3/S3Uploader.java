package server.pome.global.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadFileResult upload(MultipartFile file, String dirName) {
        try {
            String originalFilename = file.getOriginalFilename();
            String storedFileName = createStoredFileName(originalFilename);

            String key = dirName + "/" + storedFileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucket, key, file.getInputStream(), metadata);

            String fileUrl = amazonS3.getUrl(bucket, key).toString();

            return new UploadFileResult(storedFileName, fileUrl);

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }
    }

    private String createStoredFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
