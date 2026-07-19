package erp_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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