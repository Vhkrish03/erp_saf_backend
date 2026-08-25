package erp_backend.transport.controller;

import erp_backend.transport.entity.*;
import erp_backend.transport.service.TransportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminTransportController {

    private final TransportService transportService;

    public AdminTransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    // ==========================================
    // DRIVERS
    // ==========================================
    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(transportService.getAllDrivers());
    }

    @PostMapping("/drivers")
    public ResponseEntity<?> createDriver(@RequestBody Driver driver) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transportService.saveDriver(driver));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/drivers/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody Driver details) {
        try {
            Driver driver = transportService.getDriver(id)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            driver.setName(details.getName());
            driver.setEmployeeId(details.getEmployeeId());
            driver.setPhone(details.getPhone());
            driver.setLicenseNumber(details.getLicenseNumber());
            driver.setLicenseExpiry(details.getLicenseExpiry());
            driver.setStatus(details.getStatus());
            return ResponseEntity.ok(transportService.saveDriver(driver));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/drivers/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        try {
            transportService.deleteDriver(id);
            return ResponseEntity.ok(Map.of("message", "Driver deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // ==========================================
    // ROUTES
    // ==========================================
    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(transportService.getAllRoutes());
    }

    @PostMapping("/routes")
    public ResponseEntity<?> createRoute(@RequestBody Route route) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transportService.saveRoute(route));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/routes/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable Long id, @RequestBody Route details) {
        try {
            Route route = transportService.getRoute(id)
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            route.setRouteName(details.getRouteName());
            route.setDescription(details.getDescription());
            route.setStatus(details.getStatus());
            return ResponseEntity.ok(transportService.saveRoute(route));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long id) {
        try {
            transportService.deleteRoute(id);
            return ResponseEntity.ok(Map.of("message", "Route deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // ==========================================
    // BUS STOPS
    // ==========================================
    @GetMapping("/bus-stops")
    public ResponseEntity<List<BusStop>> getRouteStops(@RequestParam Long routeId) {
        return ResponseEntity.ok(transportService.getStopsForRoute(routeId));
    }

    @PostMapping("/bus-stops")
    public ResponseEntity<?> createStop(@RequestBody BusStop stop) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transportService.saveStop(stop));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/bus-stops/{id}")
    public ResponseEntity<?> updateStop(@PathVariable Long id, @RequestBody BusStop details) {
        try {
            BusStop stop = transportService.getStop(id)
                    .orElseThrow(() -> new RuntimeException("Stop not found"));
            stop.setStopName(details.getStopName());
            stop.setLatitude(details.getLatitude());
            stop.setLongitude(details.getLongitude());
            stop.setStopOrder(details.getStopOrder());
            stop.setEstimatedArrivalTime(details.getEstimatedArrivalTime());
            stop.setStatus(details.getStatus());
            return ResponseEntity.ok(transportService.saveStop(stop));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/bus-stops/{id}")
    public ResponseEntity<?> deleteStop(@PathVariable Long id) {
        try {
            transportService.deleteStop(id);
            return ResponseEntity.ok(Map.of("message", "Stop deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // ==========================================
    // BUSES
    // ==========================================
    @GetMapping("/buses")
    public ResponseEntity<List<Bus>> getAllBuses() {
        return ResponseEntity.ok(transportService.getAllBuses());
    }

    @PostMapping("/buses")
    public ResponseEntity<?> createBus(@RequestBody Map<String, Object> payload) {
        try {
            Bus bus = new Bus();
            bus.setBusNumber((String) payload.get("busNumber"));
            bus.setRegistrationNumber((String) payload.get("registrationNumber"));
            bus.setBusType((String) payload.get("busType"));
            bus.setCapacity(Integer.parseInt(payload.get("capacity").toString()));
            bus.setConductorName((String) payload.get("conductorName"));
            bus.setStatus((String) payload.get("status"));

            if (payload.get("driverId") != null) {
                Long driverId = Long.parseLong(payload.get("driverId").toString());
                Driver driver = transportService.getDriver(driverId).orElse(null);
                bus.setDriver(driver);
            }
            if (payload.get("routeId") != null) {
                Long routeId = Long.parseLong(payload.get("routeId").toString());
                Route route = transportService.getRoute(routeId).orElse(null);
                bus.setRoute(route);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(transportService.saveBus(bus));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/buses/{id}")
    public ResponseEntity<?> updateBus(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Bus bus = transportService.getBus(id)
                    .orElseThrow(() -> new RuntimeException("Bus not found"));
            bus.setBusNumber((String) payload.get("busNumber"));
            bus.setRegistrationNumber((String) payload.get("registrationNumber"));
            bus.setBusType((String) payload.get("busType"));
            bus.setCapacity(Integer.parseInt(payload.get("capacity").toString()));
            bus.setConductorName((String) payload.get("conductorName"));
            bus.setStatus((String) payload.get("status"));

            if (payload.get("driverId") != null) {
                Long driverId = Long.parseLong(payload.get("driverId").toString());
                Driver driver = transportService.getDriver(driverId).orElse(null);
                bus.setDriver(driver);
            } else {
                bus.setDriver(null);
            }
            if (payload.get("routeId") != null) {
                Long routeId = Long.parseLong(payload.get("routeId").toString());
                Route route = transportService.getRoute(routeId).orElse(null);
                bus.setRoute(route);
            } else {
                bus.setRoute(null);
            }
            return ResponseEntity.ok(transportService.saveBus(bus));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/buses/{id}")
    public ResponseEntity<?> deleteBus(@PathVariable Long id) {
        try {
            transportService.deleteBus(id);
            return ResponseEntity.ok(Map.of("message", "Bus deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // ==========================================
    // STUDENT ASSIGNMENTS
    // ==========================================
    @GetMapping("/transport-assignments")
    public ResponseEntity<List<StudentTransportAssignment>> getAllAssignments() {
        return ResponseEntity.ok(transportService.getAllAssignments());
    }

    @PostMapping("/transport-assignments")
    public ResponseEntity<?> assignStudent(@RequestBody Map<String, Object> payload) {
        try {
            String studentId = (String) payload.get("studentId");
            Long busId = Long.parseLong(payload.get("busId").toString());
            Long routeId = Long.parseLong(payload.get("routeId").toString());
            Long pickupStopId = Long.parseLong(payload.get("pickupStopId").toString());
            Long dropStopId = Long.parseLong(payload.get("dropStopId").toString());
            Long academicYearId = Long.parseLong(payload.get("academicYearId").toString());

            LocalDate startDate = null;
            if (payload.get("startDate") != null) {
                startDate = LocalDate.parse((String) payload.get("startDate"));
            }
            LocalDate endDate = null;
            if (payload.get("endDate") != null) {
                endDate = LocalDate.parse((String) payload.get("endDate"));
            }

            StudentTransportAssignment assignment = transportService.assignStudent(
                    studentId, busId, routeId, pickupStopId, dropStopId, academicYearId, startDate, endDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/transport-assignments/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {
        try {
            transportService.deactivateAssignment(id);
            return ResponseEntity.ok(Map.of("message", "Assignment deactivated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/buses/{id}/students")
    public ResponseEntity<List<StudentTransportAssignment>> getBusStudents(@PathVariable Long id) {
        return ResponseEntity.ok(transportService.getAssignmentsForBus(id));
    }

    @GetMapping("/buses/{id}/location-history")
    public ResponseEntity<List<BusLocation>> getBusLocationHistory(@PathVariable Long id) {
        return ResponseEntity.ok(transportService.getHistoryForBus(id));
    }
}
