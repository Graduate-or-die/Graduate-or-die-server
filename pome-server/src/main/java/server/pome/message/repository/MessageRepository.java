package server.pome.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


}
