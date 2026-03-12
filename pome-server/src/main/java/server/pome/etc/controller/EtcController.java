package server.pome.etc.controller;

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
import server.pome.etc.dto.request.SaveEtcRequest;
import server.pome.etc.dto.request.UpdateEtcRequest;
import server.pome.etc.dto.response.SaveUpdateEtcResponse;
import server.pome.etc.service.EtcService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/etc")
@Tag(name = "Etc", description = "포트폴리오 기타 API")
public class EtcController {

  private final EtcService etcService;

  @Operation(summary = "기타 저장")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<SaveUpdateEtcResponse>> saveEtc(
      Authentication authentication,
      @RequestPart("data") SaveEtcRequest request,
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateEtcResponse result = etcService.saveEtc(user.getId(), request, files);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponse.success(result));
  }

  @Operation(summary = "기타 수정")
  @Parameter(name = "blockId", description = "기타 블록 ID", required = true)
  @PatchMapping(value = "/{blockId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<SaveUpdateEtcResponse>> updateEtc(
      Authentication authentication,
      @PathVariable("blockId") Long blockId,
      @RequestPart("data") UpdateEtcRequest request,
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateEtcResponse result = etcService.updateEtcResponse(user.getId(), blockId, request, files);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
