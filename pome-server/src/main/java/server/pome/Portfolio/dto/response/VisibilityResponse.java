package server.pome.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisibilityResponse {
    private Long typeId;
    private boolean visible;
}
