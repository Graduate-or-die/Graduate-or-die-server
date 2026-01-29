package server.pome.attachment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadedFileInfo {

    @Schema(description = "S3 저장 파일명", example = "대회상장/00자격증")
    private String storedFileName;

    @Schema(description = "파일 URI", example = "https://s3.amazonaws.com/.../abc123.pdf")
    private String fileUrl;
}
