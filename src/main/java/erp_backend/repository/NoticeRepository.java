package erp_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}