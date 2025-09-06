package server.pome.chat.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.ChatField;
import server.pome.portfolio.type.TypeEnum;

@Repository
public interface ChatFieldRepository extends JpaRepository<ChatField, Long> {

  Optional<ChatField> findByOwner_IdAndPortfolioTypeAndBlockIdAndFieldKey(
      Long ownerId, TypeEnum portfolioType, Long blockId, String fieldKey);

  // 포트폴리오 소유자, 포트폴리오 항목으로 필드 조회
  List<ChatField> findByOwner_IdAndPortfolioType(Long ownerId, TypeEnum portfolioType);

}
