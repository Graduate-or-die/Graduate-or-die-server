package server.pome.global.infra.s3;

import java.io.InputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoredObject {
    private InputStream inputStream;
    private long contentLength;
    private String contentType;
    private String eTag;
}
