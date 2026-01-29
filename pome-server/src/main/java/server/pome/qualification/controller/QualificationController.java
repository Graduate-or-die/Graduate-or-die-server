package server.pome.qualification.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.qualification.dto.request.SaveQualificationRequest;
import server.pome.qualification.dto.request.UpdateQualificationRequest;
import server.pome.qualification.dto.response.SaveUpdateQualificationResponse;
import server.pome.qualification.service.QualificationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/capacities")
@Tag(name = "Qualification", description = "포트폴리오_자격증 API")
public class QualificationController {

    private final QualificationService qualificationService;

    @Operation(summary = "자격증 저장")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<SaveUpdateQualificationResponse>> saveQualification(Authentication authentication, @RequestPart("data") SaveQualificationRequest request, @RequestPart(value = "file", required = false) List<MultipartFile> files
    ) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        SaveUpdateQualificationResponse result = qualificationService.saveQualification(userId, request, files);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "자격증 수정")
    @Parameter(name = "blockId", description = "자격증 ID", required = true)
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<SaveUpdateQualificationResponse>> updateQualification(Authentication authentication, @RequestParam Long blockId, @RequestPart("data") UpdateQualificationRequest request, @RequestPart(value = "file", required = false) List<MultipartFile> files) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        SaveUpdateQualificationResponse result = qualificationService.updateQualification(userId, blockId, request, files);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
