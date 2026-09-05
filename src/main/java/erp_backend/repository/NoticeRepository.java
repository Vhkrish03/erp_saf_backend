package erp_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.Notice;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByDepartmentOrDepartmentOrderByIdDesc(String dept1, String dept2);
    List<Notice> findAllByOrderByIdDesc();
}