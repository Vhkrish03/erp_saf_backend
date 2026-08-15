package erp_backend.fees.repository;

import erp_backend.fees.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByStudentId(String studentId);

    List<FeePayment> findByStudentFeeId(Long studentFeeId);

    List<FeePayment> findByStudentIdAndAcademicYearAndSemester(
            String studentId, String academicYear, String semester);
}
