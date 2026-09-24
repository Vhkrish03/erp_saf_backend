package erp_backend.attendance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.attendance.dto.AttendanceSaveRequest;
import erp_backend.attendance.entity.AttendanceAuditLog;
import erp_backend.attendance.entity.AttendanceRecord;
import erp_backend.attendance.entity.AttendanceSession;
import erp_backend.attendance.entity.AttendanceStatus;
import erp_backend.attendance.repository.AttendanceAuditLogRepository;
import erp_backend.attendance.repository.AttendanceRecordRepository;
import erp_backend.attendance.repository.AttendanceSessionRepository;
import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;
import erp_backend.repository.SubjectRepository;
import erp_backend.entity.Subject;
import erp_backend.attendance.dto.StudentAttendanceSummaryDTO;
import erp_backend.attendance.dto.StudentSubjectAttendanceDTO;
import java.util.ArrayList;

import erp_backend.academics.entity.FacultySubjectAssignment;
import erp_backend.academics.repository.FacultySubjectAssignmentRepository;
import erp_backend.academics.entity.ClassInchargeAssignment;
import erp_backend.academics.repository.ClassInchargeAssignmentRepository;
import erp_backend.attendance.entity.AttendanceDelegation;
import erp_backend.attendance.repository.AttendanceDelegationRepository;

@Service
public class AttendanceCoreService {

    private final AttendanceSessionRepository sessionRepo;
    private final AttendanceRecordRepository recordRepo;
    private final AttendanceAuditLogRepository auditRepo;
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final FacultySubjectAssignmentRepository assignmentRepo;
    private final AttendanceDelegationRepository delegationRepo;
    private final ClassInchargeAssignmentRepository inchargeRepo;
    private final SubjectRepository subjectRepo;

    public AttendanceCoreService(
            AttendanceSessionRepository sessionRepo,
            AttendanceRecordRepository recordRepo,
            AttendanceAuditLogRepository auditRepo,
            TeacherRepository teacherRepo,
            StudentRepository studentRepo,
            FacultySubjectAssignmentRepository assignmentRepo,
            AttendanceDelegationRepository delegationRepo,
            ClassInchargeAssignmentRepository inchargeRepo,
            SubjectRepository subjectRepo) {
        this.sessionRepo = sessionRepo;
        this.recordRepo = recordRepo;
        this.auditRepo = auditRepo;
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
        this.assignmentRepo = assignmentRepo;
        this.delegationRepo = delegationRepo;
        this.inchargeRepo = inchargeRepo;
        this.subjectRepo = subjectRepo;
    }

    @Transactional
    public AttendanceSession saveOrUpdateAttendance(AttendanceSaveRequest request) {
        // Validation logic
        AttendanceSession session = sessionRepo.findByDepartmentAndYearAndSectionAndSubjectAndDateAndPeriod(
                request.getDepartment(), request.getYear(), request.getSection(), request.getSubject(),
                request.getDate(), request.getPeriod());

        if (session != null) {
            // Cannot edit if submitted or verified
            if (session.getStatus() != AttendanceStatus.DRAFT && session.getStatus() != AttendanceStatus.REJECTED) {
                throw new IllegalStateException("Attendance is already locked and cannot be edited by teacher.");
            }
        } else {
            session = new AttendanceSession();
            session.setDepartment(request.getDepartment());
            session.setYear(request.getYear());
            session.setSection(request.getSection());
            session.setSubject(request.getSubject());
            session.setDate(request.getDate());
            session.setPeriod(request.getPeriod());
        }

        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepo.findById(request.getTeacherId()).orElse(null);
            session.setTeacher(teacher);
        }

        session.setSubmittedBy(request.getSubmittedBy());

        if (request.getIsSubmit()) {
            session.setStatus(AttendanceStatus.SUBMITTED);
            session.setSubmittedAt(LocalDateTime.now());
        } else {
            session.setStatus(AttendanceStatus.DRAFT);
        }

        session = sessionRepo.save(session);

        // Save records
        List<AttendanceRecord> existingRecords = recordRepo.findByAttendanceSessionId(session.getId());

        for (AttendanceSaveRequest.RecordDto recDto : request.getRecords()) {
            AttendanceRecord record = existingRecords.stream()
                    .filter(r -> r.getStudent().getId().equals(recDto.getStudentId()))
                    .findFirst().orElse(null);

            if (record == null) {
                record = new AttendanceRecord();
                record.setAttendanceSession(session);
                Student student = studentRepo.findById(recDto.getStudentId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid student ID"));
                record.setStudent(student);
            }
            record.setStatus(recDto.getStatus());
            record.setMarkedAt(LocalDateTime.now());
            recordRepo.save(record);
        }

        return session;
    }

    public List<AttendanceSession> getDepartmentSessions(String department) {
        return sessionRepo.findByDepartment(department);
    }

    public List<AttendanceSession> getAllSessions() {
        return sessionRepo.findAll();
    }

    public AttendanceSession getSessionById(Long id) {
        return sessionRepo.findById(id).orElse(null);
    }

    public List<AttendanceRecord> getRecordsForSession(Long sessionId) {
        return recordRepo.findByAttendanceSessionId(sessionId);
    }

    @Transactional
    public AttendanceSession verifySession(Long sessionId, boolean isApproved, String reason, String verifiedBy) {
        AttendanceSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (isApproved) {
            session.setStatus(AttendanceStatus.ADMIN_VERIFIED);
        } else {
            session.setStatus(AttendanceStatus.REJECTED);
        }
        session.setVerificationReason(reason);
        session.setVerifiedBy(verifiedBy);
        session.setVerifiedAt(LocalDateTime.now());
        return sessionRepo.save(session);
    }

    @Transactional
    public AttendanceRecord modifyRecord(Long recordId, String newStatus, String modifiedBy, String modifierRole,
            String reason) {
        AttendanceRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));

        String oldStatus = record.getStatus();
        record.setStatus(newStatus);
        record.setMarkedAt(LocalDateTime.now());
        recordRepo.save(record);

        AttendanceAuditLog log = new AttendanceAuditLog();
        log.setAttendanceRecord(record);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setModifiedBy(modifiedBy);
        log.setModifierRole(modifierRole);
        log.setReason(reason);
        auditRepo.save(log);

        return record;
    }

    public List<AttendanceRecord> getStudentAttendance(String studentId) {
        return recordRepo.findByStudentId(studentId);
    }

    public StudentAttendanceSummaryDTO getStudentAttendanceSummary(String studentId, String academicYear) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        int semester = 1;
        try {
            semester = Integer.parseInt(student.getSemester());
        } catch (NumberFormatException e) {
            // fallback
        }

        List<Subject> subjects = subjectRepo.findByDepartmentAndYearAndSemester(
                student.getDepartment(), student.getYear(), semester);

        StudentAttendanceSummaryDTO summary = new StudentAttendanceSummaryDTO();
        List<StudentSubjectAttendanceDTO> subDtoList = new ArrayList<>();

        int totalHeld = 0;
        int totalAttended = 0;
        List<AttendanceRecord> allStudentRecords = recordRepo.findByStudentId(studentId);

        for (Subject sub : subjects) {
            // Find all sessions for the subject in this class
            List<AttendanceSession> subjectSessions = sessionRepo.findByDepartmentAndYearAndSectionAndSubject(
                    student.getDepartment(), student.getYear(), student.getSection(), sub.getName());

            // Filter out DRAFT or REJECTED sessions
            List<AttendanceSession> validSessions = subjectSessions.stream()
                    .filter(s -> s.getStatus() == AttendanceStatus.PUBLISHED ||
                            s.getStatus() == AttendanceStatus.ADMIN_VERIFIED ||
                            s.getStatus() == AttendanceStatus.SUBMITTED)
                    .toList();

            int classesHeld = validSessions.size();

            // Count how many of these valid sessions the student was PRESENT
            int classesAttended = 0;
            for (AttendanceSession s : validSessions) {
                boolean isPresent = allStudentRecords.stream()
                        .anyMatch(r -> r.getAttendanceSession().getId().equals(s.getId())
                                && ("PRESENT".equalsIgnoreCase(r.getStatus()) || "OD".equalsIgnoreCase(r.getStatus())));
                if (isPresent) {
                    classesAttended++;
                }
            }

            StudentSubjectAttendanceDTO dto = new StudentSubjectAttendanceDTO();
            dto.setSubjectCode(sub.getCode());
            dto.setSubjectName(sub.getName());
            dto.setFaculty(sub.getFaculty());
            dto.setClassesHeld(classesHeld);
            dto.setClassesAttended(classesAttended);
            dto.setAttendancePercent(classesHeld == 0 ? 0.0 : ((double) classesAttended / classesHeld) * 100);

            subDtoList.add(dto);

            totalHeld += classesHeld;
            totalAttended += classesAttended;
        }

        summary.setSubjects(subDtoList);
        summary.setTotalClassesHeld(totalHeld);
        summary.setTotalClassesAttended(totalAttended);
        summary.setOverallPercentage(totalHeld == 0 ? 0.0 : ((double) totalAttended / totalHeld) * 100);

        return summary;
    }

    public List<FacultySubjectAssignment> getTeacherAssignments(String employeeId) {
        return assignmentRepo.findByTeacherEmployeeId(employeeId);
    }

    public List<AttendanceDelegation> getTeacherDelegations(String employeeId) {
        Teacher teacher = teacherRepo.findByEmployeeId(employeeId).orElse(null);
        if (teacher == null) {
            return List.of();
        }
        return delegationRepo.findByAssignedToIdAndStatus(teacher.getId(), "ACCEPTED"); // Or whatever valid statuses
    }

    public List<ClassInchargeAssignment> getTeacherInchargeAssignments(String employeeId) {
        return inchargeRepo.findByTeacher_EmployeeIdOrderByCreatedAtDesc(employeeId).stream()
                .filter(ClassInchargeAssignment::isActive)
                .toList();
    }
}
