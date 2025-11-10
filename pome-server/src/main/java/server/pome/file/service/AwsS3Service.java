package server.pome.file.service;

import static server.pome.global.exception.BaseResponseStatus.S3_ERROR;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;
  private final AmazonS3 amazonS3;

  public List<String> uploadFile(List<MultipartFile> multipartFiles) {
    List<String> fileNameList = new ArrayList<>();

    multipartFiles.forEach(file -> {
      String fileName = createFileName(file.getOriginalFilename());
      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentLength(file.getSize());
      objectMetadata.setContentType(file.getContentType());

      try (InputStream inputStream = file.getInputStream()) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));
      } catch (IOException e){
        throw new BaseException(S3_ERROR);
      }
      fileNameList.add(fileName);
    });

    return fileNameList;
  }

  public String createFileName(String fileName) {
    return UUID.randomUUID().toString().concat(getFileExtension(fileName));
  }


  // 파일명에서 . 이후의 스트링 = 확장자 가져옴
  public String getFileExtension(String fileName) {
    try {
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new BaseException(BaseResponseStatus.INVALID_REQUEST_FORM, "파일 형식 오류");
    }
  }

  public void deleteFile(String fileName) {
    amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    System.out.println(bucket);
  }
}
