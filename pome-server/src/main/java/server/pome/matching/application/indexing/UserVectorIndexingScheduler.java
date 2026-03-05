package server.pome.matching.application.indexing;

import java.util.concurrent.Semaphore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.pome.matching.domain.EmbeddingStatus;
import server.pome.matching.infrastructure.repository.UserVectorStateRepository;

@Component
@RequiredArgsConstructor
public class UserVectorIndexingScheduler {

  private final UserVectorStateRepository stateRepository;
  private final UserVectorIndexingService indexingService;
  private final Semaphore semaphore;

  @Transactional
  @Scheduled(fixedDelayString = "${vector-indexing.fixed-delay-ms:3000}")
  public void run() {
    if (!semaphore.tryAcquire()) {
      return;
    }
    try {
      doRun();
    } finally {
      semaphore.release();
    }
  }

  protected void doRun() {
    int batchSize = Integer.parseInt(System.getProperty("POME_VECTOR_BATCH", "50"));

    var pending = stateRepository.findForUpdate(
        EmbeddingStatus.PENDING,
        PageRequest.of(0, batchSize)
    );

    for (var state : pending) {
      indexingService.indexOne(state);
    }
  }
}
