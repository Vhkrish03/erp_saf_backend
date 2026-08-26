package erp_backend.transport.service;

import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;
import erp_backend.academics.entity.AcademicYear;
import erp_backend.academics.repository.AcademicYearRepository;
import erp_backend.transport.entity.*;
import erp_backend.transport.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransportService {

    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;
    private final BusStopRepository busStopRepository;
    private final BusRepository busRepository;
    private final StudentTransportAssignmentRepository assignmentRepository;
    private final BusLocationRepository locationRepository;
    private final BusCurrentLocationRepository currentLocationRepository;
    private final StudentRepository studentRepository;
    private final AcademicYearRepository academicYearRepository;

    public TransportService(
            DriverRepository driverRepository,
            RouteRepository routeRepository,
            BusStopRepository busStopRepository,
            BusRepository busRepository,
            StudentTransportAssignmentRepository assignmentRepository,
            BusLocationRepository locationRepository,
            BusCurrentLocationRepository currentLocationRepository,
            StudentRepository studentRepository,
            AcademicYearRepository academicYearRepository) {
        this.driverRepository = driverRepository;
        this.routeRepository = routeRepository;
        this.busStopRepository = busStopRepository;
        this.busRepository = busRepository;
        this.assignmentRepository = assignmentRepository;
        this.locationRepository = locationRepository;
        this.currentLocationRepository = currentLocationRepository;
        this.studentRepository = studentRepository;
        this.academicYearRepository = academicYearRepository;
    }

    // ==========================================
    // DRIVERS
    // ==========================================
    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriver(Long id) {
        return driverRepository.findById(id);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

    // ==========================================
    // ROUTES
    // ==========================================
    public Route saveRoute(Route route) {
        return routeRepository.save(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Optional<Route> getRoute(Long id) {
        return routeRepository.findById(id);
    }

    public void deleteRoute(Long id) {
        assignmentRepository.deleteByRouteId(id);
        busStopRepository.deleteByRouteId(id);
        routeRepository.deleteById(id);
    }

    // ==========================================
    // BUS STOPS
    // ==========================================
    public BusStop saveStop(BusStop stop) {
        return busStopRepository.save(stop);
    }

    public List<BusStop> getStopsForRoute(Long routeId) {
        return busStopRepository.findByRouteIdOrderByStopOrderAsc(routeId);
    }

    public Optional<BusStop> getStop(Long id) {
        return busStopRepository.findById(id);
    }

    public void deleteStop(Long id) {
        busStopRepository.deleteById(id);
    }

    public void deleteStopsForRoute(Long routeId) {
        busStopRepository.deleteByRouteId(routeId);
    }

    // ==========================================
    // BUSES
    // ==========================================
    public Bus saveBus(Bus bus) {
        return busRepository.save(bus);
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Optional<Bus> getBus(Long id) {
        return busRepository.findById(id);
    }

    public void deleteBus(Long id) {
        assignmentRepository.deleteByBusId(id);
        busRepository.deleteById(id);
    }

    // ==========================================
    // STUDENT ASSIGNMENTS & TRANSPORTS
    // ==========================================
    public StudentTransportAssignment assignStudent(
            String studentId,
            Long busId,
            Long routeId,
            Long pickupStopId,
            Long dropStopId,
            Long academicYearId,
            LocalDate startDate,
            LocalDate endDate) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        if ("HOSTELLER".equalsIgnoreCase(student.getResidencyType())) {
            throw new RuntimeException("Hostellers are ineligible for college bus transportation.");
        }

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus not found: " + busId));

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found: " + routeId));

        BusStop pickupStop = busStopRepository.findById(pickupStopId)
                .orElseThrow(() -> new RuntimeException("Pickup stop not found: " + pickupStopId));

        BusStop dropStop = busStopRepository.findById(dropStopId)
                .orElseThrow(() -> new RuntimeException("Drop stop not found: " + dropStopId));

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new RuntimeException("Academic year not found: " + academicYearId));

        // Capacity validation
        long activeCount = assignmentRepository.countActiveAssignmentsByBusId(busId);
        if (activeCount >= bus.getCapacity()) {
            throw new RuntimeException("Bus capacity has been reached.");
        }

        // Deactivate any existing active assignment for this student (to preserve
        // history)
        Optional<StudentTransportAssignment> existingAssign = assignmentRepository.findByStudentIdAndStatus(studentId,
                "ACTIVE");
        if (existingAssign.isPresent()) {
            StudentTransportAssignment current = existingAssign.get();
            current.setStatus("INACTIVE");
            current.setEndDate(LocalDate.now());
            assignmentRepository.save(current);
        }

        StudentTransportAssignment newAssign = new StudentTransportAssignment();
        newAssign.setStudent(student);
        newAssign.setBus(bus);
        newAssign.setRoute(route);
        newAssign.setPickupStop(pickupStop);
        newAssign.setDropStop(dropStop);
        newAssign.setAcademicYear(academicYear);
        newAssign.setStartDate(startDate != null ? startDate : LocalDate.now());
        newAssign.setEndDate(endDate);
        newAssign.setStatus("ACTIVE");

        // Update student profile
        student.setTransportRequired(true);
        student.setTransportStatus("ACTIVE");
        studentRepository.save(student);

        return assignmentRepository.save(newAssign);
    }

    public void deactivateAssignment(Long assignmentId) {
        StudentTransportAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found: " + assignmentId));

        assignment.setStatus("INACTIVE");
        assignment.setEndDate(LocalDate.now());
        assignmentRepository.save(assignment);

        Student student = assignment.getStudent();
        student.setTransportRequired(false);
        student.setTransportStatus("NOT_ASSIGNED");
        studentRepository.save(student);
    }

    public List<StudentTransportAssignment> getAssignmentsForStudent(String studentId) {
        return assignmentRepository.findByStudentId(studentId);
    }

    public Optional<StudentTransportAssignment> getActiveAssignmentForStudent(String studentId) {
        return assignmentRepository.findByStudentIdAndStatus(studentId, "ACTIVE");
    }

    public List<StudentTransportAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public List<StudentTransportAssignment> getAssignmentsForBus(Long busId) {
        return assignmentRepository.findByBusIdAndStatus(busId, "ACTIVE");
    }

    // ==========================================
    // TRACKING & LOCATION
    // ==========================================
    public void updateBusLocation(Long busId, Double latitude, Double longitude, Double speed, Double heading,
            String status) {
        // Validation: Verify the bus exists
        if (!busRepository.existsById(busId)) {
            throw new RuntimeException("Bus not found with ID: " + busId);
        }

        // Save location log to history
        BusLocation locationLog = new BusLocation();
        locationLog.setBusId(busId);
        locationLog.setLatitude(latitude);
        locationLog.setLongitude(longitude);
        locationLog.setSpeed(speed);
        locationLog.setHeading(heading);
        locationRepository.save(locationLog);

        // Update current location
        BusCurrentLocation curr = currentLocationRepository.findByBusId(busId)
                .orElse(new BusCurrentLocation());
        curr.setBusId(busId);
        curr.setLatitude(latitude);
        curr.setLongitude(longitude);
        curr.setSpeed(speed);
        curr.setHeading(heading);
        curr.setLastUpdated(LocalDateTime.now());
        if (status != null && !status.isEmpty()) {
            curr.setStatus(status);
        } else {
            curr.setStatus("ON_ROUTE"); // Default status during GPS update
        }
        currentLocationRepository.save(curr);
    }

    public Optional<BusCurrentLocation> getBusCurrentLocation(Long busId) {
        return currentLocationRepository.findByBusId(busId);
    }

    public List<BusLocation> getHistoryForBus(Long busId) {
        return locationRepository.findByBusIdOrderByRecordedAtDesc(busId);
    }
}
