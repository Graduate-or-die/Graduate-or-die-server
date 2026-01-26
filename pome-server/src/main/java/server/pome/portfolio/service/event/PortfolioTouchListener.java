package server.pome.portfolio.service.event;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import server.pome.global.domain.Portfolio;
import server.pome.portfolio.service.PortfolioService;

@Component
@RequiredArgsConstructor
public class PortfolioTouchListener {

  private final PortfolioService portfolioService;
  private final ApplicationEventPublisher publisher;
  private final EntityManager entityManager;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handle(PortfolioUpdateRequestedEvent event) {
    Portfolio portfolio = portfolioService.getPortfolio(event.userId());
    portfolio.touch();
    entityManager.flush();

    publisher.publishEvent(new PortfolioUpdatedEvent(
        event.userId(),
        portfolio.getId(),
        portfolio.getVersion()
    ));
  }
}
