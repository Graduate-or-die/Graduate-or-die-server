package server.pome.attachment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.pome.attachment.dto.response.AttachmentResponse;
import server.pome.attachment.service.AttachmentService;
import server.pome.global.domain.BaseResponse;
import server.pome.global.domain.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolios/attachments")
@Tag(name = "Attachment", description = "S3 파일")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(summary = "파일 삭제")
    @DeleteMapping
    public ResponseEntity<BaseResponse<String>> deleteFile(
            Authentication authentication,
            @Parameter(description = "타입 ID", required = true) @RequestParam Long typeId,
            @Parameter(description = "블록 ID", required = true) @RequestParam Long blockId
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        attachmentService.deleteFile(userId, typeId, blockId);

        return ResponseEntity.ok(BaseResponse.success(null));
    }
}
