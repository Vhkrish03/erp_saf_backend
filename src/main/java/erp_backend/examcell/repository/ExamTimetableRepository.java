package erp_backend.examcell.repository;

import erp_backend.examcell.entity.ExamTimetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamTimetableRepository extends JpaRepository<ExamTimetable, Long> {
    List<ExamTimetable> findByExaminationId(Long examinationId);

    List<ExamTimetable> findByExaminationIdOrderByExamDateAsc(Long examinationId);
}
