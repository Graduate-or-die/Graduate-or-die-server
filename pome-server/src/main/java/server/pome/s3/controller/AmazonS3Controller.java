package server.pome.s3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import server.pome.global.domain.BaseResponse;
import server.pome.s3.service.AwsS3Service;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class AmazonS3Controller {

  private final AwsS3Service awsS3Service;

  @Operation(summary = "파일 저장")
  @Parameter(name = "multipartFiles", description = "저장할 파일 목록")
  @PostMapping
  public ResponseEntity<BaseResponse<List<String>>> uploadFile(List<MultipartFile> multipartFiles) {
    return ResponseEntity.ok(BaseResponse.success(awsS3Service.uploadFile(multipartFiles)));
  }

  @Operation(summary = "파일 삭제")
  @Parameter(name = "fileName", description = "삭제할 파일명")
  @DeleteMapping
  public ResponseEntity<BaseResponse<String>> deleteFile(@RequestParam String fileName) {
    awsS3Service.deleteFile(fileName);
    return ResponseEntity.ok(BaseResponse.success(fileName));
  }
}
