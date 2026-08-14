package erp_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.entity.Timetable;
import erp_backend.repository.TimetableRepository;

@Service
public class TimetableService {

    private final TimetableRepository repository;

    public TimetableService(TimetableRepository repository) {
        this.repository = repository;
    }

    public List<Timetable> getAllTimetable() {
        return repository.findAll();
    }

    public List<Timetable> getByDay(String day) {
        return repository.findByDay(day);
    }

    public List<Timetable> getByDayAndFilters(String day, String department, String year, String semester,
            String section, String academicYear) {
        if (department == null || year == null || semester == null || section == null) {
            return repository.findByDay(day);
        }
        if (academicYear != null && !academicYear.isEmpty()) {
            return repository.findByDayAndDepartmentAndYearAndSemesterAndSectionAndAcademicYear(day, department, year,
                    semester, section, academicYear);
        } else {
            return repository.findByDayAndDepartmentAndYearAndSemesterAndSection(day, department, year, semester,
                    section);
        }
    }

    public List<Timetable> getStudentTimetable(String department, String year, String semester, String section,
            String academicYear) {
        if (academicYear != null && !academicYear.isEmpty()) {
            return repository.findByDepartmentAndYearAndSemesterAndSectionAndAcademicYear(department, year, semester,
                    section, academicYear);
        } else {
            return repository.findByDepartmentAndYearAndSemesterAndSection(department, year, semester, section);
        }
    }

    // Retrieve the timetable schedule for the logged-in teacher.
    public List<Timetable> getTeacherTimetable(String employeeId) {
        return repository.findByEmployeeId(employeeId);
    }
}