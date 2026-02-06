package server.pome.attachment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.pome.global.domain.Attachment;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findByPortfolio_IdAndTypeIdAndBlockId(
            Long portfolioId,
            Long typeId,
            Long blockId
    );
}