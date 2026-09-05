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
        List<Notice> all = repository.findAllByOrderByIdDesc();
        if (role == null) return all;
        String upperRole = role.toUpperCase();
        
        return all.stream().filter(n -> {
            String noticeDept = n.getDepartment() == null ? "ALL" : n.getDepartment();
            String noticeStatus = n.getStatus() == null ? "APPROVED" : n.getStatus();
            
            boolean isDeptMatch = department == null || noticeDept.equalsIgnoreCase("ALL") || noticeDept.equalsIgnoreCase(department);
            
            if (upperRole.equals("ADMIN") || upperRole.equals("SUPER_ADMIN") || upperRole.equals("DEAN")) {
                return true; 
            }
            if (upperRole.equals("HOD") || upperRole.equals("TEACHER") || upperRole.equals("FACULTY")) {
                return isDeptMatch; 
            }
            return isDeptMatch && "APPROVED".equals(noticeStatus);
        }).toList();
    }

    public Notice createNotice(Notice notice) {
        String role = notice.getUploaderRole() != null ? notice.getUploaderRole().toUpperCase() : "";
        if (role.equals("TEACHER") || role.equals("FACULTY")) {
            notice.setStatus("PENDING_HOD");
        } else if (role.equals("HOD")) {
            notice.setStatus("PENDING_ADMIN");
        } else {
            notice.setStatus("APPROVED");
        }
        return repository.save(notice);
    }

    public Notice approveNotice(Long id, String role) {
        Notice notice = repository.findById(id).orElse(null);
        if (notice == null) return null;
        String upper = role.toUpperCase();
        
        if ("HOD".equals(upper) && "PENDING_HOD".equals(notice.getStatus())) {
            notice.setStatus("PENDING_ADMIN");
            return repository.save(notice);
        }
        if ("ADMIN".equals(upper) || "SUPER_ADMIN".equals(upper) || "DEAN".equals(upper)) {
            if ("PENDING_ADMIN".equals(notice.getStatus()) || "PENDING_HOD".equals(notice.getStatus())) {
                notice.setStatus("APPROVED");
                return repository.save(notice);
            }
        }
        return notice;
    }
}
