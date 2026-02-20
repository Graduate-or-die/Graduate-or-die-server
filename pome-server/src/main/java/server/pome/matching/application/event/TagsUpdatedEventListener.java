package server.pome.matching.application.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.pome.matching.domain.UserVectorState;
import server.pome.matching.infrastructure.repository.UserVectorStateRepository;

@Component
@RequiredArgsConstructor
public class TagsUpdatedEventListener {

  private final UserVectorStateRepository stateRepository;

  @EventListener
  @Transactional
  public void onTagsUpdated(TagsUpdatedEvent event) {
    var state = stateRepository.findById(event.userId())
        .orElseGet(() -> new UserVectorState(event.userId(), event.version()));
    state.markPending(event.version());
    stateRepository.save(state);
  }
}
