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

    public List<Notice> getNotices(String role, String department) {
        if (role == null) return repository.findAllByOrderByIdDesc();
        
        String upperRole = role.toUpperCase();
        if (upperRole.equals("ADMIN") || upperRole.equals("SUPER_ADMIN") || upperRole.equals("DEAN")) {
            return repository.findAllByOrderByIdDesc();
        }
        
        if (department == null || department.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
        }
        
        return repository.findByDepartmentOrDepartmentOrderByIdDesc(department, "ALL");
    }

    public Notice createNotice(Notice notice) {
        return repository.save(notice);
    }
}
