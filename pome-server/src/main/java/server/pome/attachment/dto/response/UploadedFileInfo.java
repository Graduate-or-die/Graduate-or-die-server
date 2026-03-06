package server.pome.attachment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadedFileInfo {

    @Schema(description = "S3 객체 key", example = "portfolio/123/550e8400-e29b-41d4-a716-446655440000.pdf")
    private String storedKey;
}
