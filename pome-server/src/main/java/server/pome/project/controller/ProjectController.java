package server.pome.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;
import server.pome.project.dto.request.SaveProjectRequest;
import server.pome.project.dto.request.UpdateProjectRequest;
import server.pome.project.dto.response.SaveUpdateProjectResponse;
import server.pome.project.service.ProjectService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolios/projects")
@Tag(name ="Project", description = "포트폴리오 프로젝트 API")
public class ProjectController {

  private final ProjectService projectService;

  @Operation(summary = "프로젝트 저장")
  @PostMapping
  public ResponseEntity<BaseResponse<SaveUpdateProjectResponse>> saveProject(
      Authentication authentication,
      @Valid @RequestBody SaveProjectRequest request
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateProjectResponse result = projectService.saveProject(user.getId(), request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(BaseResponse.success(result));
  }

  @Operation(summary = "프로젝트 수정")
  @Parameter(name = "blockId", description = "프로젝트 블록 ID", required = true)
  @PatchMapping("/{blockId}")
  public ResponseEntity<BaseResponse<SaveUpdateProjectResponse>> updateProject(
      Authentication authentication,
      @PathVariable("blockId") Long blockId,
      @Valid @RequestBody UpdateProjectRequest request
  ) {
    User user = (User) authentication.getPrincipal();
    SaveUpdateProjectResponse result = projectService.updateProject(user.getId(), blockId, request);
    return ResponseEntity.ok(BaseResponse.success(result));
  }
}
