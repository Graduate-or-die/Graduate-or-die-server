package server.pome.attachment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @Operation(summary = "파일 첨부")
    @GetMapping
    public ResponseEntity<BaseResponse<List<AttachmentResponse>>> getAttachments(
            Authentication authentication,
            @RequestParam Long typeId,
            @RequestParam Long blockId
    ) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        List<AttachmentResponse> result =
                attachmentService.getAttachments(
                        userId,
                        typeId,
                        blockId
                );

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
