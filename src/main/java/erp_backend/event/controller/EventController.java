package erp_backend.event.controller;

import erp_backend.event.dto.EventCreateRequest;
import erp_backend.event.dto.EventResponse;
import erp_backend.event.dto.EventUpdateRequest;
import erp_backend.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreateRequest request) {
        EventResponse created = eventService.createEvent(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @RequestBody EventUpdateRequest request,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        EventResponse updated = eventService.updateEvent(id, request, userId, referenceId, role);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        EventResponse event = eventService.getEvent(id);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        eventService.deleteEvent(id, userId, referenceId, role);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<EventResponse> submitEvent(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        EventResponse submitted = eventService.submitEvent(id, userId, referenceId, role);
        return ResponseEntity.ok(submitted);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<EventResponse> approveEvent(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        EventResponse approved = eventService.approveEvent(id, userId, referenceId, role);
        return ResponseEntity.ok(approved);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<EventResponse> rejectEvent(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        EventResponse rejected = eventService.rejectEvent(id, reason, userId, referenceId, role);
        return ResponseEntity.ok(rejected);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publishEvent(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        EventResponse published = eventService.publishEvent(id, userId, referenceId, role);
        return ResponseEntity.ok(published);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        EventResponse cancelled = eventService.cancelEvent(id, userId, referenceId, role);
        return ResponseEntity.ok(cancelled);
    }

    // List of events matching requesting user's targeted filters
    @GetMapping
    public ResponseEntity<List<EventResponse>> getEventsForUser(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        List<EventResponse> events = eventService.getEventsForUser(userId, referenceId, role);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<EventResponse>> getMyEvents(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        List<EventResponse> myEvents = eventService.getMyEvents(userId, referenceId, role);
        return ResponseEntity.ok(myEvents);
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<List<EventResponse>> getPendingApprovals(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String role) {
        List<EventResponse> pending = eventService.getPendingApprovals(userId, referenceId, role);
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<EventResponse>> getDepartmentEvents(@PathVariable String department) {
        List<EventResponse> deptEvents = eventService.getDepartmentEvents(department);
        return ResponseEntity.ok(deptEvents);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        List<EventResponse> upcoming = eventService.getUpcomingEvents();
        return ResponseEntity.ok(upcoming);
    }

    @GetMapping("/published")
    public ResponseEntity<List<EventResponse>> getPublishedEvents() {
        List<EventResponse> published = eventService.getPublishedEvents();
        return ResponseEntity.ok(published);
    }
}
