package server.pome.matching.infrastructure.adapter;

import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.pome.global.exception.BaseException;
import server.pome.matching.application.indexing.UserEmbeddingTextPort;
import server.pome.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserEmbeddingTextAdapter implements UserEmbeddingTextPort {

  private final UserRepository userRepository;

  @Override
  public String getUserEmbeddingText(long userId, String embeddingVersion) {
    var user = userRepository.findById(userId)
        .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

    String tagText = user.getTags().stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(tag -> !tag.isBlank())
        .distinct()
        .sorted(Comparator.naturalOrder())
        .collect(Collectors.joining(" "));

    if (tagText.isBlank()) {
      return "TAGS: NONE";
    }
    return "TAGS: " + tagText;
  }
}
