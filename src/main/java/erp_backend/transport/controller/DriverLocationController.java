package erp_backend.transport.controller;

import erp_backend.transport.service.TransportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bus-tracking")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DriverLocationController {

    private final TransportService transportService;

    public DriverLocationController(TransportService transportService) {
        this.transportService = transportService;
    }

    @PostMapping("/location")
    public ResponseEntity<?> updateLocation(@RequestBody Map<String, Object> payload) {
        try {
            Long busId = Long.parseLong(payload.get("busId").toString());
            Double latitude = Double.parseDouble(payload.get("latitude").toString());
            Double longitude = Double.parseDouble(payload.get("longitude").toString());
            Double speed = payload.get("speed") != null ? Double.parseDouble(payload.get("speed").toString()) : 0.0;
            Double heading = payload.get("heading") != null ? Double.parseDouble(payload.get("heading").toString())
                    : 0.0;
            String status = (String) payload.get("status");

            transportService.updateBusLocation(busId, latitude, longitude, speed, heading, status);
            return ResponseEntity.ok(Map.of("message", "Location updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
