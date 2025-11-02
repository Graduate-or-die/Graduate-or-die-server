package server.pome.message.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


  Page<Message> findByMessageRoomIdOrderByIdAsc(Long MessageRoomId, Pageable pageable);

}
