package server.pome.attachment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import server.pome.file.FileDownloadUrls;
import server.pome.global.domain.Attachment;

@Getter
@AllArgsConstructor
public class FileResponse {

    @Schema(description = "첨부 파일 ID", example = "10")
    private Long fileId;

    @Schema(description = "원본 파일명", example = "certificate.pdf")
    private String originalFileName;

    @Schema(description = "다운로드 URL", example = "/files/portfolio/10")
    private String downloadUrl;

    public static FileResponse from(Attachment attachment) {
        return new FileResponse(
                attachment.getId(),
                attachment.getOriginalFileName(),
                FileDownloadUrls.portfolioAttachment(attachment.getId())
        );
    }
}