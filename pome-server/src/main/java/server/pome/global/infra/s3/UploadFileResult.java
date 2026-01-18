package server.pome.global.infra.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadFileResult {
    private String storedFileName; // S3에 저장된 파일명
    private String fileUrl;
}
