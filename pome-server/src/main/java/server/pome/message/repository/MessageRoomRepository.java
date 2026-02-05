package server.pome.message.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.MessageRoom;

@Repository
public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {
  Optional<MessageRoom> findByUserLowIdAndUserHighId(Long userId, Long mateId);
}
