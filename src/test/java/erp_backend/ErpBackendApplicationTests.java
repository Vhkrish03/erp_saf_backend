package erp_backend;

import erp_backend.examcell.entity.ExamCellResult;
import erp_backend.examcell.entity.ExamCellResultSubject;
import erp_backend.examcell.service.ExamCellService;
import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ErpBackendApplicationTests {

	@Autowired
	private ExamCellService examCellService;

	@Autowired
	private StudentRepository studentRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void testSaveNewExamCellResultSuccess() {
		// Clean / setup test student
		if (!studentRepository.existsById("ST001")) {
			Student student = new Student();
			student.setId("ST001");
			student.setName("John Doe");
			studentRepository.save(student);
		}

		// Prepare Result
		ExamCellResult result = new ExamCellResult();
		result.setStudentId("ST001");
		result.setDepartment("CSE");
		result.setSemesterName("S5");
		result.setAcademicYear("2025-26");
		result.setExamSession("Nov/Dec 2025");
		result.setStatus("DRAFT");

		// Prepare subjects
		List<ExamCellResultSubject> subjects = new ArrayList<>();
		ExamCellResultSubject sub1 = new ExamCellResultSubject();
		sub1.setSubjectCode("CS301");
		sub1.setSubjectName("Database Systems");
		sub1.setCredits(3);
		sub1.setGrade("A");
		sub1.setResultStatus("PASS");
		subjects.add(sub1);

		result.setSubjects(subjects);

		// Save result
		ExamCellResult saved = examCellService.saveOrUpdateResult(result, "EMP001", "EXAM_CELL");

		assertNotNull(saved);
		assertNotNull(saved.getId());
		assertEquals(1, saved.getSubjects().size());
		assertNotNull(saved.getSubjects().get(0).getExamCellResult());
		assertEquals(saved.getId(), saved.getSubjects().get(0).getExamCellResult().getId());
	}
}
