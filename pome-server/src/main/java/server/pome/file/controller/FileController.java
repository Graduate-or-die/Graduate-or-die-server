package server.pome.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pome.file.service.FileDownload;
import server.pome.file.service.FileService;
import server.pome.global.domain.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
@Tag(name = "File", description = "파일 스트리밍 API")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "프로필 이미지 조회")
    @GetMapping("/profile")
    public ResponseEntity<InputStreamResource> getProfileImage(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        FileDownload download = fileService.downloadProfileImage(user.getId());
        return buildResponse(download);
    }

    @Operation(summary = "포트폴리오 첨부 조회/다운로드")
    @GetMapping("/portfolio/{attachmentId}")
    public ResponseEntity<InputStreamResource> getPortfolioAttachment(
            Authentication authentication,
            @PathVariable Long attachmentId
    ) {
        User user = (User) authentication.getPrincipal();
        FileDownload download = fileService.downloadPortfolioAttachment(user.getId(), attachmentId);
        return buildResponse(download);
    }

    private ResponseEntity<InputStreamResource> buildResponse(FileDownload download) {
        HttpHeaders headers = new HttpHeaders();
        if (download.getContentLength() >= 0) {
            headers.setContentLength(download.getContentLength());
        }
        headers.setContentType(MediaType.parseMediaType(download.getContentType()));

        ContentDisposition disposition = download.isInline()
                ? ContentDisposition.inline().build()
                : ContentDisposition.attachment()
                .filename(resolveFileName(download), StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(disposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(download.getInputStream()));
    }

    private String resolveFileName(FileDownload download) {
        if (download.getOriginalFileName() != null && !download.getOriginalFileName().isBlank()) {
            return download.getOriginalFileName();
        }
        return "file";
    }
}
