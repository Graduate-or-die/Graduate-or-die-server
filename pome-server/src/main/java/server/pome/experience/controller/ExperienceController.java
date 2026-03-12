package server.pome.experience.controller;

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
import server.pome.experience.dto.request.SaveExperienceRequest;
import server.pome.experience.dto.request.UpdateExperienceRequest;
import server.pome.experience.dto.response.SaveUpdateExperienceResponse;
import server.pome.experience.service.ExperienceService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/experiences")
@Tag(name = "Experience", description = "포트폴리오 경력 API")
public class ExperienceController {

  private final ExperienceService experienceService;

  @Operation(summary = "경력 저장")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<SaveUpdateExperienceResponse>> saveExperience(
      Authentication authentication,
      @RequestPart("data") SaveExperienceRequest request,
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateExperienceResponse result = experienceService.saveExperience(user.getId(), request, files);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponse.success(result));
  }

  @Operation(summary = "경력 수정")
  @Parameter(name = "blockId", description = "경력 블록 ID", required = true)
  @PatchMapping(value = "/{blockId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<SaveUpdateExperienceResponse>> updateExperience(
      Authentication authentication,
      @PathVariable("blockId") Long blockId,
      @RequestPart("data") UpdateExperienceRequest request,
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateExperienceResponse result = experienceService.updateExperience(user.getId(), blockId, request, files);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
