package server.pome.portfolio.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
public class BulkDeletePortfolioBlockRequest {

    @NotNull
    private Long typeId;

    @NotEmpty
    private List<Long> blockIds;
}
