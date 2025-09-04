package server.pome.qualification.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.Qualification;
import server.pome.qualification.dto.request.SaveQualificationRequest;
import server.pome.qualification.dto.request.UpdateQualificationRequest;
import server.pome.qualification.dto.response.SaveQualificationResponse;
import server.pome.qualification.dto.response.UpdateQualificationResponse;
import server.pome.qualification.service.QualificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/capacities")
@Tag(name = "Qualification", description = "포트폴리오_자격증 API")
public class QualificationController {

    private final QualificationService qualificationService;

    @Operation(summary = "자격증 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveQualificationResponse>> saveQualification(@PathVariable Long userId, @Valid @RequestBody SaveQualificationRequest request) {

        SaveQualificationResponse result = qualificationService.saveQualification(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "자격증 수정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @Parameter(name = "blockId", description = "자격증 ID", required = true)
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<UpdateQualificationResponse>> updateQualification(@PathVariable Long userId, @RequestParam("blockId") Long blockId, @Valid @RequestBody UpdateQualificationRequest request) {

        UpdateQualificationResponse result = qualificationService.updateQualification(userId, blockId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
