package erp_backend.Messages.controller;

import erp_backend.Messages.model.Message;
import erp_backend.Messages.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        try {
            Message saved = messageService.sendMessage(message);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/inbox/{receiverId}")
    public ResponseEntity<?> getInbox(@PathVariable String receiverId) {
        return ResponseEntity.ok(messageService.getInboxForUser(receiverId));
    }

    @GetMapping("/sent/{senderId}")
    public ResponseEntity<?> getSentMessages(@PathVariable String senderId) {
        return ResponseEntity.ok(messageService.getSentMessages(senderId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllMessagesForAdmin() {
        return ResponseEntity.ok(messageService.getAllMessagesForAdmin());
    }

    @PostMapping("/mark-read/receiver/{id}")
    public ResponseEntity<?> markAsReadByReceiver(@PathVariable Long id) {
        try {
            messageService.markAsReadByReceiver(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/mark-read/admin/{id}")
    public ResponseEntity<?> markAsReadByAdmin(@PathVariable Long id) {
        try {
            messageService.markAsReadByAdmin(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
