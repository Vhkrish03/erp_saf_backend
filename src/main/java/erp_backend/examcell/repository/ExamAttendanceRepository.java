package erp_backend.examcell.repository;

import erp_backend.examcell.entity.ExamAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttendanceRepository extends JpaRepository<ExamAttendance, Long> {
    List<ExamAttendance> findByExamTimetableId(Long examTimetableId);

    Optional<ExamAttendance> findByExamTimetableIdAndStudentId(Long examTimetableId, String studentId);
}
