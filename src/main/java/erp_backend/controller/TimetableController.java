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
    public List<Timetable> getByDay(@PathVariable String day) {
        return service.getByDay(day);
    }
}