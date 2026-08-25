package erp_backend.transport.controller;

import erp_backend.transport.entity.*;
import erp_backend.transport.service.TransportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StudentTransportController {

    private final TransportService transportService;

    public StudentTransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    private StudentTransportAssignment getActiveStudentAssignment(String referenceId, String role) {
        if (referenceId == null || role == null || !"STUDENT".equalsIgnoreCase(role)) {
            throw new RuntimeException("Unauthorized: Access restricted to students.");
        }
        return transportService.getActiveAssignmentForStudent(referenceId)
                .orElseThrow(() -> new RuntimeException("No active bus transport assignment found for this student."));
    }

    @GetMapping("/transport")
    public ResponseEntity<?> getTransportProfile(
            @RequestParam String referenceId,
            @RequestParam String role) {
        try {
            StudentTransportAssignment assignment = getActiveStudentAssignment(referenceId, role);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("status", "NOT_ASSIGNED", "message", e.getMessage()));
        }
    }

    @GetMapping("/my-bus/location")
    public ResponseEntity<?> getMyBusLocation(
            @RequestParam String referenceId,
            @RequestParam String role) {
        try {
            StudentTransportAssignment assignment = getActiveStudentAssignment(referenceId, role);
            Long busId = assignment.getBus().getId();
            BusCurrentLocation loc = transportService.getBusCurrentLocation(busId)
                    .orElseThrow(() -> new RuntimeException("Location tracking not started for this bus."));
            return ResponseEntity.ok(loc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-bus/route")
    public ResponseEntity<?> getMyBusRoute(
            @RequestParam String referenceId,
            @RequestParam String role) {
        try {
            StudentTransportAssignment assignment = getActiveStudentAssignment(referenceId, role);
            return ResponseEntity.ok(assignment.getRoute());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-bus/stops")
    public ResponseEntity<?> getMyBusStops(
            @RequestParam String referenceId,
            @RequestParam String role) {
        try {
            StudentTransportAssignment assignment = getActiveStudentAssignment(referenceId, role);
            List<BusStop> stops = transportService.getStopsForRoute(assignment.getRoute().getId());
            return ResponseEntity.ok(stops);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}
