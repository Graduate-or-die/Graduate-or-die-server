package server.pome.attachment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import server.pome.global.domain.Attachment;

public class FileResponse {
    @Schema(description = "파일 이름", example = "s3관련자료")
    private String fileName;
    @Schema(description = "파일 URI", example = "https://s3.amazonaws.com/.../abc123.pdf")
    private String fileUrl;

    public FileResponse(String fileName, String fileUrl) {

        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public static FileResponse from(Attachment attachment) {
        return new FileResponse(
                attachment.getStoredFileName(),
                attachment.getFileUrl()
        );
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
