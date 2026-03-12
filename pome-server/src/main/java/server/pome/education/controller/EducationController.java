package server.pome.education.controller;

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
import server.pome.education.dto.request.SaveEducationRequest;
import server.pome.education.dto.request.UpdateEducationRequest;
import server.pome.education.dto.response.SaveUpdateEducationResponse;
import server.pome.education.service.EducationService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/educations")
@Tag(name = "Education", description = "포트폴리오 학력 API")
public class EducationController {

  private final EducationService educationService;

  @Operation(summary = "학력 저장")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<SaveUpdateEducationResponse>> saveEducation(
      Authentication authentication,
      @RequestPart("data") SaveEducationRequest request,
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateEducationResponse result = educationService.saveEducation(user.getId(), request, files);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponse.success(result));
  }

  @Operation(summary = "학력 수정")
  @Parameter(name = "blockId", description = "학력 블록 ID", required = true)
  @PatchMapping(value = "/{blockId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<SaveUpdateEducationResponse>> updateEducation(
      Authentication authentication,
      @PathVariable("blockId") Long blockId,
      @RequestPart("data") UpdateEducationRequest request,
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateEducationResponse result = educationService.updateEducation(user.getId(), blockId, request, files);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
