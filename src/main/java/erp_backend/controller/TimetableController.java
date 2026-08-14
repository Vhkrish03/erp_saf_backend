package erp_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import erp_backend.entity.Timetable;
import erp_backend.service.TimetableService;

@RestController
@RequestMapping("/api/timetable")
@CrossOrigin("*")
public class TimetableController {

    private final TimetableService service;

    public TimetableController(TimetableService service) {
        this.service = service;
    }

    @GetMapping
    public List<Timetable> getAll() {
        return service.getAllTimetable();
    }

    @GetMapping("/{day}")
    public List<Timetable> getByDay(
            @PathVariable String day,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String academicYear) {
        return service.getByDayAndFilters(day, department, year, semester, section, academicYear);
    }

    @GetMapping("/student")
    public List<Timetable> getStudentTimetable(
            @RequestParam String department,
            @RequestParam String year,
            @RequestParam String semester,
            @RequestParam String section,
            @RequestParam(required = false) String academicYear) {
        return service.getStudentTimetable(department, year, semester, section, academicYear);
    }

    // Get timetable details for a specific teacher based on Employee ID.
    @GetMapping("/teacher/{employeeId}")
    public List<Timetable> getTeacherTimetable(@PathVariable String employeeId) {
        return service.getTeacherTimetable(employeeId);
    }

    @GetMapping("/test01")
    public Timetable test() {

        Timetable t = new Timetable();

        t.setId(1L);
        t.setDay("Monday");
        t.setTime("9:00 - 9:50");
        t.setSubject("Operating Systems");
        t.setRoom("A-201");
        t.setFaculty("Dr. Meera");

        return t;
    }

}