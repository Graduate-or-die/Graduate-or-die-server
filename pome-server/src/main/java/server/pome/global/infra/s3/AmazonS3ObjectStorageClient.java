package server.pome.global.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;

@Component
@RequiredArgsConstructor
public class AmazonS3ObjectStorageClient {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public StoredObject getObject(String key) {
        try {
            // S3 객체와 메타데이터를 읽어서 서비스 공통 객체로 변환
            S3Object object = amazonS3.getObject(bucket, key);
            ObjectMetadata metadata = object.getObjectMetadata();
            return new StoredObject(
                    object.getObjectContent(),
                    metadata.getContentLength(),
                    metadata.getContentType(),
                    metadata.getETag()
            );
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                throw new BaseException(BaseResponseStatus.FILE_RESOURCE_NOT_FOUND);
            }
            throw new BaseException(BaseResponseStatus.S3_ERROR);
        }
    }
}
