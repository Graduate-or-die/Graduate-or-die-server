package server.pome.matching.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.pome.global.domain.UserMatch;
import server.pome.matching.infrastructure.repository.UserMatchRepository;

@Service
@RequiredArgsConstructor
public class UserMatchService {

  private final UserMatchRepository userMatchRepository;

  @Transactional
  public void recordMatch(long a, long b) {
    UserMatch pair = UserMatch.of(a, b);
    boolean exists = userMatchRepository.existsByUserLowAndUserHigh(pair.getUserLow(),
        pair.getUserHigh());
    if (exists) {
      return;
    }

    userMatchRepository.save(pair);
  }

  @Transactional
  public void removeMatch(long a, long b) {
    UserMatch pair = UserMatch.of(a, b);
    userMatchRepository.deleteByUserLowAndUserHigh(pair.getUserLow(), pair.getUserHigh());
  }
}
