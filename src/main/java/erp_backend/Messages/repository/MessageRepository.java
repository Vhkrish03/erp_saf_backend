package erp_backend.Messages.repository;

import erp_backend.Messages.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdOrderByTimestampDesc(String receiverId);

    List<Message> findBySenderIdOrderByTimestampDesc(String senderId);

    List<Message> findAllByOrderByTimestampDesc();
}
