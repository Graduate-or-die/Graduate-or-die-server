package server.pome.education.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.pome.education.dto.request.SaveEducationRequest;
import server.pome.education.dto.request.UpdateEducationRequest;
import server.pome.education.dto.response.SaveUpdateEducationResponse;
import server.pome.education.service.EducationService;
import server.pome.global.domain.BaseResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/educations")
@Tag(name = "Education", description = "포트폴리오_학력 API")
public class EducationController {

    private final EducationService educationService;

    @Operation(summary = "학력 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveUpdateEducationResponse>> saveEducation(@PathVariable Long userId, @Valid @RequestBody SaveEducationRequest request) {

        SaveUpdateEducationResponse result = educationService.saveEducation(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "학력 수정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveUpdateEducationResponse>> updateEducation(@PathVariable Long userId, @Valid @RequestBody UpdateEducationRequest request) {

        SaveUpdateEducationResponse result = educationService.updateEducation(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
