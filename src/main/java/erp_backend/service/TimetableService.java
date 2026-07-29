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

    // Retrieve the timetable schedule for the logged-in teacher.
    public List<Timetable> getTeacherTimetable(String employeeId) {
        return repository.findByEmployeeId(employeeId);
    }
}