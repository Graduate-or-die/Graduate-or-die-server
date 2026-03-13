package server.pome.matching.application.recommend;

import java.util.List;
import java.util.Set;

public interface CandidateFilterPort {

  Set<Long> filterEligible(long me, List<Long> candidateIds);
}
