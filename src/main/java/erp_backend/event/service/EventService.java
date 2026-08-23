package erp_backend.event.service;

import erp_backend.Hod.entity.Hod;
import erp_backend.Hod.repository.HodRepository;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.entity.Student;
import erp_backend.entity.User;
import erp_backend.event.dto.EventCreateRequest;
import erp_backend.event.dto.EventResponse;
import erp_backend.event.dto.EventUpdateRequest;
import erp_backend.event.entity.Event;
import erp_backend.event.enums.EventStatus;
import erp_backend.event.enums.EventType;
import erp_backend.event.repository.EventRepository;
import erp_backend.repository.StudentRepository;
import erp_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final HodRepository hodRepository;

    public EventService(EventRepository eventRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            HodRepository hodRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.hodRepository = hodRepository;
    }

    public User resolveUser(Long userId, String referenceId, String role) {
        if (userId != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found by ID: " + userId));
        }
        if (referenceId != null && role != null) {
            String tempRole = role.toUpperCase().trim();
            if (tempRole.equals("TEACHER")) {
                tempRole = "FACULTY";
            }
            final String dbRole = tempRole;
            return userRepository.findByReferenceIdAndRole(referenceId, dbRole)
                    .orElseThrow(() -> new RuntimeException(
                            "User not found by referenceId: " + referenceId + " and role: " + dbRole));
        }
        throw new RuntimeException("Missing user identification parameters (userId or referenceId & role is required)");
    }

    public EventResponse createEvent(EventCreateRequest req) {
        User creator = resolveUser(req.getCreatedByUserId(), req.getCreatedByReferenceId(), req.getCreatedByRole());

        Event event = new Event();
        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        event.setEventType(EventType.valueOf(req.getEventType()));
        event.setOrganizerName(req.getOrganizerName());
        event.setOrganizerDepartment(req.getOrganizerDepartment());
        event.setVenue(req.getVenue());
        event.setEventDate(req.getEventDate());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setRegistrationStartDate(req.getRegistrationStartDate());
        event.setRegistrationEndDate(req.getRegistrationEndDate());
        event.setRegistrationRequired(req.getRegistrationRequired() != null ? req.getRegistrationRequired() : false);
        event.setRegistrationLink(req.getRegistrationLink());
        event.setContactPerson(req.getContactPerson());
        event.setContactEmail(req.getContactEmail());
        event.setContactPhone(req.getContactPhone());
        event.setEligibility(req.getEligibility());
        event.setTargetAudience(req.getTargetAudience());
        event.setDepartment(req.getDepartment());
        event.setYear(req.getYear());
        event.setSection(req.getSection());
        event.setImageUrl(req.getImageUrl());
        event.setAttachmentUrl(req.getAttachmentUrl());
        event.setCreatedBy(creator);

        String role = creator.getRole().toUpperCase();
        event.setCreatedByRole(role);

        // All users default to DRAFT, except custom dashboard scenarios.
        // HODs / Admins can publish draft events using publish endpoint.
        event.setStatus(EventStatus.DRAFT);

        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    public EventResponse updateEvent(Long id, EventUpdateRequest req, Long requestUserId, String referenceId,
            String role) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User requester = resolveUser(requestUserId, referenceId, role);

        // Authorization: Must be creator OR HOD/Admin of the same department
        boolean isCreator = event.getCreatedBy().getId().equals(requester.getId());
        boolean isHod = requester.getRole().equalsIgnoreCase("HOD") && requester.getReferenceId() != null;
        boolean isAdmin = requester.getRole().equalsIgnoreCase("ADMIN")
                || requester.getRole().equalsIgnoreCase("SUPER_ADMIN");

        if (!isCreator && !isAdmin) {
            if (isHod) {
                Hod hod = hodRepository.findByEmployeeId(requester.getReferenceId())
                        .orElseThrow(() -> new RuntimeException("HOD details not found"));
                if (!hod.getDepartment().equalsIgnoreCase(event.getOrganizerDepartment())) {
                    throw new RuntimeException("Unauthorized: HOD can only edit department events");
                }
            } else {
                throw new RuntimeException("Unauthorized to edit this event");
            }
        }

        // Prevent modification of published/cancelled events unless creator is
        // Admin/SuperAdmin
        if (!isAdmin && (event.getStatus() == EventStatus.PUBLISHED || event.getStatus() == EventStatus.CANCELLED)) {
            throw new RuntimeException("Cannot edit event in " + event.getStatus() + " status");
        }

        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        if (req.getEventType() != null) {
            event.setEventType(EventType.valueOf(req.getEventType()));
        }
        event.setOrganizerName(req.getOrganizerName());
        event.setOrganizerDepartment(req.getOrganizerDepartment());
        event.setVenue(req.getVenue());
        event.setEventDate(req.getEventDate());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setRegistrationStartDate(req.getRegistrationStartDate());
        event.setRegistrationEndDate(req.getRegistrationEndDate());
        if (req.getRegistrationRequired() != null) {
            event.setRegistrationRequired(req.getRegistrationRequired());
        }
        event.setRegistrationLink(req.getRegistrationLink());
        event.setContactPerson(req.getContactPerson());
        event.setContactEmail(req.getContactEmail());
        event.setContactPhone(req.getContactPhone());
        event.setEligibility(req.getEligibility());
        event.setTargetAudience(req.getTargetAudience());
        event.setDepartment(req.getDepartment());
        event.setYear(req.getYear());
        event.setSection(req.getSection());
        event.setImageUrl(req.getImageUrl());
        event.setAttachmentUrl(req.getAttachmentUrl());

        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    public EventResponse submitEvent(Long id, Long requestUserId, String referenceId, String role) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User requester = resolveUser(requestUserId, referenceId, role);

        if (!event.getCreatedBy().getId().equals(requester.getId())) {
            throw new RuntimeException("Only the creator can submit this event for approval");
        }

        if (event.getStatus() != EventStatus.DRAFT && event.getStatus() != EventStatus.REJECTED) {
            throw new RuntimeException("Can only submit events that are in DRAFT or REJECTED status");
        }

        event.setStatus(EventStatus.PENDING_APPROVAL);
        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    public EventResponse approveEvent(Long id, Long requestUserId, String referenceId, String role) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User requester = resolveUser(requestUserId, referenceId, role);

        String userRole = requester.getRole().toUpperCase();
        if (!userRole.equals("HOD") && !userRole.equals("ADMIN") && !userRole.equals("SUPER_ADMIN")) {
            throw new RuntimeException("Only HOD or Admin can approve events");
        }

        if (userRole.equals("HOD")) {
            Hod hod = hodRepository.findByEmployeeId(requester.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("HOD details not found"));
            if (!hod.getDepartment().equalsIgnoreCase(event.getOrganizerDepartment())) {
                throw new RuntimeException("HOD can only approve events from their department");
            }
        }

        event.setStatus(EventStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    public EventResponse rejectEvent(Long id, String rejectionReason, Long requestUserId, String referenceId,
            String role) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User requester = resolveUser(requestUserId, referenceId, role);

        String userRole = requester.getRole().toUpperCase();
        if (!userRole.equals("HOD") && !userRole.equals("ADMIN") && !userRole.equals("SUPER_ADMIN")) {
            throw new RuntimeException("Only HOD or Admin can reject events");
        }

        if (userRole.equals("HOD")) {
            Hod hod = hodRepository.findByEmployeeId(requester.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("HOD details not found"));
            if (!hod.getDepartment().equalsIgnoreCase(event.getOrganizerDepartment())) {
                throw new RuntimeException("HOD can only reject events from their department");
            }
        }

        event.setStatus(EventStatus.REJECTED);
        event.setRejectionReason(rejectionReason);
        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    public EventResponse publishEvent(Long id, Long requestUserId, String referenceId, String role) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User requester = resolveUser(requestUserId, referenceId, role);

        String userRole = requester.getRole().toUpperCase();

        boolean allowed = userRole.equals("ADMIN") || userRole.equals("SUPER_ADMIN");
        if (!allowed && userRole.equals("HOD")) {
            Hod hod = hodRepository.findByEmployeeId(requester.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("HOD details not found"));
            if (hod.getDepartment().equalsIgnoreCase(event.getOrganizerDepartment())) {
                allowed = true;
            }
        }

        if (!allowed) {
            throw new RuntimeException("Unauthorized: You do not have permissions to publish this event");
        }

        event.setStatus(EventStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    public EventResponse cancelEvent(Long id, Long requestUserId, String referenceId, String role) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User requester = resolveUser(requestUserId, referenceId, role);

        String userRole = requester.getRole().toUpperCase();
        boolean isCreator = event.getCreatedBy().getId().equals(requester.getId());
        boolean allowed = isCreator || userRole.equals("ADMIN") || userRole.equals("SUPER_ADMIN");

        if (!allowed && userRole.equals("HOD")) {
            Hod hod = hodRepository.findByEmployeeId(requester.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("HOD details not found"));
            if (hod.getDepartment().equalsIgnoreCase(event.getOrganizerDepartment())) {
                allowed = true;
            }
        }

        if (!allowed) {
            throw new RuntimeException("Unauthorized to cancel this event");
        }

        event.setStatus(EventStatus.CANCELLED);
        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    public void deleteEvent(Long id, Long requestUserId, String referenceId, String role) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User requester = resolveUser(requestUserId, referenceId, role);

        String userRole = requester.getRole().toUpperCase();
        boolean isCreator = event.getCreatedBy().getId().equals(requester.getId());
        boolean allowed = isCreator || userRole.equals("ADMIN") || userRole.equals("SUPER_ADMIN");

        if (!allowed && userRole.equals("HOD")) {
            Hod hod = hodRepository.findByEmployeeId(requester.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("HOD details not found"));
            if (hod.getDepartment().equalsIgnoreCase(event.getOrganizerDepartment())) {
                allowed = true;
            }
        }

        if (!allowed) {
            throw new RuntimeException("Unauthorized to delete this event");
        }

        eventRepository.delete(event);
    }

    public EventResponse getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return EventResponse.fromEntity(event);
    }

    public List<EventResponse> getMyEvents(Long userId, String referenceId, String role) {
        User user = resolveUser(userId, referenceId, role);
        return eventRepository.findByCreatedById(user.getId()).stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getPendingApprovals(Long requestUserId, String referenceId, String role) {
        User requester = resolveUser(requestUserId, referenceId, role);

        String userRole = requester.getRole().toUpperCase();
        if (!userRole.equals("HOD") && !userRole.equals("ADMIN") && !userRole.equals("SUPER_ADMIN")
                && !userRole.equals("FACULTY")) {
            throw new RuntimeException("Only HOD/Admin/Faculty can view approvals");
        }

        if (userRole.equals("HOD")) {
            Hod hod = hodRepository.findByEmployeeId(requester.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("HOD details not found"));
            return eventRepository.findByStatusAndOrganizerDepartment(EventStatus.PENDING_APPROVAL, hod.getDepartment())
                    .stream()
                    .map(EventResponse::fromEntity)
                    .collect(Collectors.toList());
        } else {
            // Under normal ERP rules, they could see all pending approvals or filtered
            // ones.
            return eventRepository.findByStatus(EventStatus.PENDING_APPROVAL).stream()
                    .map(EventResponse::fromEntity)
                    .collect(Collectors.toList());
        }
    }

    public List<EventResponse> getDepartmentEvents(String department) {
        return eventRepository.findByOrganizerDepartment(department).stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getPublishedEvents() {
        return eventRepository.findAllPublishedEvents().stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findAllPublishedEvents().stream()
                .filter(e -> !e.getEventDate().isBefore(today))
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsForUser(Long userId, String referenceId, String role) {
        User user = resolveUser(userId, referenceId, role);

        String userRole = user.getRole().toUpperCase();
        if (userRole.equals("STUDENT")) {
            Student student = studentRepository.findById(user.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("Student details not found"));
            return eventRepository.findPublishedEventsForStudent(
                    student.getDepartment(),
                    student.getYear(),
                    student.getSection()).stream().map(EventResponse::fromEntity).collect(Collectors.toList());
        } else if (userRole.equals("TEACHER") || userRole.equals("FACULTY")) {
            Teacher teacher = teacherRepository.findByEmployeeId(user.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("Teacher details not found"));
            return eventRepository.findPublishedEventsForTeacher(teacher.getDepartment()).stream()
                    .map(EventResponse::fromEntity)
                    .collect(Collectors.toList());
        } else if (userRole.equals("HOD")) {
            Hod hod = hodRepository.findByEmployeeId(user.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("HOD details not found"));
            return eventRepository.findPublishedEventsForTeacher(hod.getDepartment()).stream()
                    .map(EventResponse::fromEntity)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findAllPublishedEvents().stream()
                    .map(EventResponse::fromEntity)
                    .collect(Collectors.toList());
        }
    }
}
