package erp_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findByDay(String day);

    // Fetch all timetable entries assigned to a specific teacher using Employee ID
    List<Timetable> findByEmployeeId(String employeeId);

    List<Timetable> findByDepartmentAndYearAndSemesterAndSection(String department, String year, String semester,
            String section);

    List<Timetable> findByDepartmentAndYearAndSemesterAndSectionAndAcademicYear(String department, String year,
            String semester, String section, String academicYear);

    List<Timetable> findByDayAndDepartmentAndYearAndSemesterAndSection(String day, String department, String year,
            String semester, String section);

    List<Timetable> findByDayAndDepartmentAndYearAndSemesterAndSectionAndAcademicYear(String day, String department,
            String year, String semester, String section, String academicYear);

}