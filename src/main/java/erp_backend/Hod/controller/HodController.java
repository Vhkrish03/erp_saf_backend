package erp_backend.Hod.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.Hod.entity.Hod;
import erp_backend.Hod.service.HodService;

@RestController
@RequestMapping("/api/hod")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HodController {

    private final HodService service;

    public HodController(HodService service) {
        this.service = service;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Hod> getHodDetails(@PathVariable String employeeId) {
        Hod hod = service.getHodByEmployeeId(employeeId);
        if (hod == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(hod);
    }
}
