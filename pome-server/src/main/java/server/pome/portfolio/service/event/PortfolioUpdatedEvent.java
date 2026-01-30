package server.pome.portfolio.service.event;

public record PortfolioUpdatedEvent (
  Long userId,
  Long portfolioId,
  Long portfolioVersion
) {}
