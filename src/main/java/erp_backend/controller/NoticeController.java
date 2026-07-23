package erp_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.entity.Notice;
import erp_backend.service.NoticeService;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin("*")
public class NoticeController {

    private final NoticeService service;

    public NoticeController(NoticeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Notice> getAllNotices() {
        return service.getAllNotices();
    }
}
