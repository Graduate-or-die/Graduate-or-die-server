package server.pome.attachment.service;

import static server.pome.global.exception.BaseResponseStatus.S3_ERROR;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.dto.response.UploadedFileInfo;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;
  private final AmazonS3 amazonS3;

  public UploadedFileInfo uploadFile(MultipartFile file, String dir) {
    String fileName = dir + "/" + createFileName(file.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());

    try (InputStream inputStream = file.getInputStream()) {
      amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
    } catch (IOException e){
      throw new BaseException(S3_ERROR);
    }

    String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
    return new UploadedFileInfo(fileName, fileUrl);
  }

  public void deleteFile(String fileName) {
    amazonS3.deleteObject(bucket, fileName);
  }

  public String createFileName(String fileName) {
    return UUID.randomUUID().toString().concat(getFileExtension(fileName));
  }

  public String getFileExtension(String fileName) {
    try {
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new BaseException(BaseResponseStatus.INVALID_REQUEST_FORM, "파일 형식 오류");
    }
  }
}