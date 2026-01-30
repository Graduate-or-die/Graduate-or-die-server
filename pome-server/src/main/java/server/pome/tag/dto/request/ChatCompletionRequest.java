package server.pome.tag.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionRequest(
    String model,
    List<Message> messages,
    Double temperature
) {
  public record Message(String role, String content) {}
}
