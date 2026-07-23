package erp_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.entity.Notice;
import erp_backend.repository.NoticeRepository;

@Service
public class NoticeService {

    private final NoticeRepository repository;

    public NoticeService(NoticeRepository repository) {
        this.repository = repository;
    }

    public List<Notice> getAllNotices() {
        return repository.findAll();
    }
}
