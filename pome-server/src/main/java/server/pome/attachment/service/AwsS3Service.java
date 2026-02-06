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

  // 유저 이미지 관련
  public void deleteFileByUrl(String imageUrl) {
    if (!isOurS3Image(imageUrl)) return;

    String storedFileName = extractFileName(imageUrl);
    deleteFile(storedFileName);
  }

  public String createFileName(String fileName) {
    return UUID.randomUUID().toString().concat(getFileExtension(fileName));
  }

  public boolean isOurS3Image(String imageUrl) {
    if (imageUrl == null || imageUrl.isBlank()) return false;
    return imageUrl.contains(bucket);
  }

  public String extractFileName(String imageUrl) {
    return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
  }

  private String getFileExtension(String fileName) {
    try {
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new BaseException(INVALID_REQUEST_FORM);
    }
  }
}
