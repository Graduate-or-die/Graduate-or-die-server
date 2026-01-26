package server.pome.portfolio.service.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
@Component
@AllArgsConstructor
@Slf4j
public class PortfolioUpdateNotifier {

  private final ApplicationEventPublisher publisher;

  public void notifyUpdated(Long userId) {
    publisher.publishEvent(new PortfolioUpdateRequestedEvent(userId));
  }
}
