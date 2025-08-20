package server.pome.qualification.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.Qualification;
import server.pome.qualification.dto.request.SaveQualificationRequest;
import server.pome.qualification.dto.response.SaveQualificationResponse;
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
}
