package server.pome.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.pome.global.domain.BaseResponse;
import server.pome.project.dto.request.SaveProjectRequest;
import server.pome.project.dto.request.UpdateProjectRequest;
import server.pome.project.dto.response.SaveProjectResponse;
import server.pome.project.dto.response.UpdateProjectResponse;
import server.pome.project.service.ProjectService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/projects")
@Tag(name ="Project", description = "포트폴리오 프로젝트 API")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 저장")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<SaveProjectResponse>> saveProject(@PathVariable Long userId, @Valid @RequestBody SaveProjectRequest request) {
        SaveProjectResponse result = projectService.saveProject(userId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "프로젝트 수정")
    @Parameter(name = "userId", description = "회원 ID", required = true)
    @Parameter(name = "blockId", description = "프로젝트 ID", required = true)
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<UpdateProjectResponse>> updateProject(@PathVariable Long userId, @RequestParam("blockId") Long blockId, @Valid @RequestBody UpdateProjectRequest request) {
        UpdateProjectResponse result = projectService.updateProject(userId, blockId, request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

}
