package server.pome.portfolio.service.event;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static server.pome.global.exception.BaseResponseStatus.USER_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import server.pome.tag.TagGenerator;
import server.pome.global.domain.User;
import server.pome.global.exception.BaseException;
import server.pome.portfolio.dto.response.GetAllPortfolioResponse;
import server.pome.portfolio.service.PortfolioService;
import server.pome.user.repository.UserRepository;

@Component
@Slf4j
@AllArgsConstructor
public class PortfolioTagUpdateListener {

  private final UserRepository userRepository;
  private final PortfolioService portfolioService;
  private final TagGenerator tagGenerator;

  @Async
  @Transactional(propagation = REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(PortfolioUpdatedEvent event) {
    try {
      log.info("[TAG_EVT] listener entered userId={}, thread={}",
          event.userId(), Thread.currentThread().getName());

      log.info("[TAG_EVT] step1 load user userId={}", event.userId());
      User user = userRepository.findById(event.userId())
          .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

      // 최신성 역전 방지
      Long current = user.getTagsUpdatedPortfolioVersion();
      if (current != null && event.portfolioVersion() <= current) {
        log.info("[TAG_EVT] listener stopped userId={}, version={}", event.userId(),
            event.portfolioVersion());
        return;
      }

      log.info("[TAG_EVT] step2 load portfolio userId={}", event.userId());
      // 포트폴리오 전체 조회
      GetAllPortfolioResponse all = portfolioService.getAllPortfolioResponse(event.userId());

      log.info("[TAG_EVT] step3 generate tags userId={}", event.userId());
      // 태그 생성
      List<String> tags = tagGenerator.generateTags(all);
      log.info("[TAG_EVT] step3 done tags={}", tags);

      log.info("[TAG_EVT] step4 update+save userId={}", event.userId());
      // 저장, 시각 업데이트
      user.updateTags(tags, event.portfolioVersion());
    } catch (Throwable throwable) {
      log.error("[TAG_EVT] tag update failed userId={}, version={}", event.userId(), event.portfolioVersion(), throwable);
    }
  }
}
