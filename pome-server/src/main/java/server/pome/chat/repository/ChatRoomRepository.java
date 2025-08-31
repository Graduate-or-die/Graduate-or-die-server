package server.pome.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.pome.global.domain.ChatRoom;
import server.pome.global.domain.ChatRoomRead;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


}
