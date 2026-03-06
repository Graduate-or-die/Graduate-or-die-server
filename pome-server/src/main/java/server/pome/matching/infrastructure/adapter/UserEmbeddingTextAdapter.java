package server.pome.matching.infrastructure.adapter;

import org.springframework.stereotype.Component;
import server.pome.matching.application.indexing.UserEmbeddingTextPort;

@Component
public class UserEmbeddingTextAdapter implements UserEmbeddingTextPort {

  @Override
  public String getUserEmbeddingText(long userId, String embeddingVersion) {
    // TODO: 태그, 포트폴리오 내용 가져와서 #tag1, #tag2로 정제
    return "";
  }
}
