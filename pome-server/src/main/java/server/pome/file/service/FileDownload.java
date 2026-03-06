package server.pome.file.service;

import java.io.InputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileDownload {
    private InputStream inputStream;
    private long contentLength;
    private String contentType;
    private String originalFileName;
    private boolean inline;
}
