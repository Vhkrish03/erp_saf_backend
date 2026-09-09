package erp_backend.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.notice.entity.Notice;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByDepartmentOrDepartmentOrderByIdDesc(String dept1, String dept2);

    List<Notice> findAllByOrderByIdDesc();
}