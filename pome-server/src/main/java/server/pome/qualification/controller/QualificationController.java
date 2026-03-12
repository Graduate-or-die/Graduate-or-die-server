package server.pome.qualification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.qualification.dto.request.SaveQualificationRequest;
import server.pome.qualification.dto.request.UpdateQualificationRequest;
import server.pome.qualification.dto.response.SaveUpdateQualificationResponse;
import server.pome.qualification.service.QualificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/capacities")
@Tag(name = "Qualification", description = "포트폴리오 자격증 API")
public class QualificationController {

  private final QualificationService qualificationService;

  @Operation(summary = "자격증 저장")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<SaveUpdateQualificationResponse>> saveQualification(
      Authentication authentication,
      @RequestPart("data") SaveQualificationRequest request,
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateQualificationResponse result = qualificationService.saveQualification(user.getId(), request, files);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponse.success(result));
  }

  @Operation(summary = "자격증 수정")
  @Parameter(name = "blockId", description = "자격증 블록 ID", required = true)
  @PatchMapping(value = "/{blockId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<SaveUpdateQualificationResponse>> updateQualification(
      Authentication authentication,
      @PathVariable("blockId") Long blockId,
      @RequestPart("data") UpdateQualificationRequest request,
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateQualificationResponse result = qualificationService.updateQualification(user.getId(), blockId, request, files);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
