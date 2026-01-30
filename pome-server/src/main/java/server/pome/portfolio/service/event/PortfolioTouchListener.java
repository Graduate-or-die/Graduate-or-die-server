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
    // 포트폴리오 조회
    Portfolio portfolio = portfolioService.getPortfolio(event.userId());

    // 포트폴리오 버전 업데이트
    portfolio.touch();
    entityManager.flush();

    // 포트폴리오 수정 이벤트 발행
    publisher.publishEvent(new PortfolioUpdatedEvent(
        event.userId(),
        portfolio.getId(),
        portfolio.getVersion()
    ));
  }
}
