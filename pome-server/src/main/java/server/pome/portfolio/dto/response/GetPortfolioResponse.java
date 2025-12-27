package server.pome.portfolio.dto.response;

import server.pome.global.enums.TypeEnum;

public record GetPortfolioResponse(
  TypeEnum type,
  Object item
) {}
