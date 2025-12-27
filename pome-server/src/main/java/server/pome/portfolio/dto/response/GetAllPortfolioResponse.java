package server.pome.portfolio.dto.response;

import java.util.Map;
import server.pome.global.enums.TypeEnum;

public record GetAllPortfolioResponse(
    Long userId,
    Long portfolioId,
    Map<TypeEnum, Object> sections
) {}

