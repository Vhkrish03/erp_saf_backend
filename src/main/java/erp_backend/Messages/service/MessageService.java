package erp_backend.Messages.service;

import erp_backend.Messages.model.Message;
import erp_backend.Messages.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getInboxForUser(String receiverId) {
        return messageRepository.findByReceiverIdOrderByTimestampDesc(receiverId);
    }

    public List<Message> getSentMessages(String senderId) {
        return messageRepository.findBySenderIdOrderByTimestampDesc(senderId);
    }

    public List<Message> getAllMessagesForAdmin() {
        return messageRepository.findAllByOrderByTimestampDesc();
    }

    public void markAsReadByReceiver(Long id) {
        Message msg = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        msg.setReadByReceiver(true);
        messageRepository.save(msg);
    }

    public void markAsReadByAdmin(Long id) {
        Message msg = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        msg.setReadByAdmin(true);
        messageRepository.save(msg);
    }
}
