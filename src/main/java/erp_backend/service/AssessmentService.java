package erp_backend.service;

import erp_backend.entity.*;
import erp_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentComponentRepository componentRepository;
    private final AssessmentMarkRepository markRepository;
    private final AssessmentWeightageRepository weightageRepository;
    private final AssessmentWorkflowRepository workflowRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public AssessmentService(AssessmentRepository assessmentRepository,
            AssessmentComponentRepository componentRepository,
            AssessmentMarkRepository markRepository,
            AssessmentWeightageRepository weightageRepository,
            AssessmentWorkflowRepository workflowRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository) {
        this.assessmentRepository = assessmentRepository;
        this.componentRepository = componentRepository;
        this.markRepository = markRepository;
        this.weightageRepository = weightageRepository;
        this.workflowRepository = workflowRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    // Initialize Default Weightages if not exists
    @Transactional
    public void ensureDefaultWeightgages(String department, String semester) {
        List<AssessmentWeightage> current = weightageRepository.findByDepartmentAndSemester(department, semester);
        if (current.isEmpty()) {
            weightageRepository.save(new AssessmentWeightage(department, semester, "WRITTEN", 0.50));
            weightageRepository.save(new AssessmentWeightage(department, semester, "ASSIGNMENT", 0.20));
            weightageRepository.save(new AssessmentWeightage(department, semester, "SEMINAR", 0.15));
            weightageRepository.save(new AssessmentWeightage(department, semester, "QUIZ", 0.15));
        }
    }

    // Fetch weekly tests match section
    public List<Assessment> getWeeklyAssessments(String department, String semester, String section) {
        return assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(department, semester, section,
                "WEEKLY");
    }

    // Fetch IAT assessments match section
    public List<Assessment> getIatAssessments(String department, String semester, String section) {
        return assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(department, semester, section, "IAT");
    }

    public List<Assessment> getAssessmentsByFaculty(String facultyId) {
        return assessmentRepository.findByFacultyId(facultyId);
    }

    public Optional<Assessment> getAssessmentById(Long id) {
        return assessmentRepository.findById(id);
    }

    // Get students in the target class
    public List<Student> getStudentsForAssessment(Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found."));
        return studentRepository.findByDepartmentAndSemesterAndSection(
                assessment.getDepartment(), assessment.getSemester(), assessment.getSection());
    }

    // Create an assessment (by class incharge or admin)
    @Transactional
    public Assessment createAssessment(Assessment assessment) {
        if (assessment.getSubject() != null) {
            Subject dbSubject = null;
            if (assessment.getSubject().getId() != null) {
                dbSubject = subjectRepository.findById(assessment.getSubject().getId()).orElse(null);
            }
            if (dbSubject == null && assessment.getSubject().getCode() != null) {
                dbSubject = subjectRepository.findByCode(assessment.getSubject().getCode()).orElse(null);
            }
            if (dbSubject == null) {
                Subject defaultSub = new Subject();
                defaultSub.setCode(
                        assessment.getSubject().getCode() != null ? assessment.getSubject().getCode() : "CS8791");
                defaultSub.setName(assessment.getSubject().getName() != null ? assessment.getSubject().getName()
                        : "Cloud Computing");
                defaultSub
                        .setFaculty(assessment.getSubject().getFaculty() != null ? assessment.getSubject().getFaculty()
                                : "Vinitha Mam");
                defaultSub.setCredits(3);
                dbSubject = subjectRepository.save(defaultSub);
            }
            assessment.setSubject(dbSubject);
        }
        assessment.setStatus("DRAFT");
        Assessment saved = assessmentRepository.save(assessment);

        // Auto-create workflow state
        AssessmentWorkflow workflow = new AssessmentWorkflow();
        workflow.setAssessment(saved);
        workflow.setFacultyStatus("DRAFT");
        workflow.setClassInchargeStatus("PENDING");
        workflow.setHodStatus("PENDING");
        workflow.setDeanStatus("PENDING");
        workflowRepository.save(workflow);
        saved.setWorkflow(workflow);

        // Check if IAT and initialize components
        if ("IAT".equalsIgnoreCase(saved.getType())) {
            ensureDefaultWeightgages(saved.getDepartment(), saved.getSemester());
            List<AssessmentWeightage> wtList = weightageRepository.findByDepartmentAndSemester(saved.getDepartment(),
                    saved.getSemester());

            List<AssessmentComponent> components = new ArrayList<>();
            for (AssessmentWeightage wt : wtList) {
                AssessmentComponent comp = new AssessmentComponent();
                comp.setAssessment(saved);
                comp.setComponentType(wt.getComponentType());
                comp.setWeightage(wt.getWeightage());
                // Default Max Marks per component: WRITTEN=50, Quiz/Assign/Seminar=10
                if ("WRITTEN".equalsIgnoreCase(wt.getComponentType())) {
                    comp.setMaxMarks(50);
                } else {
                    comp.setMaxMarks(10);
                }
                components.add(componentRepository.save(comp));
            }
            saved.setComponents(components);
        } else {
            // For WEEKLY test, create a default major component representing the test
            // itself
            AssessmentComponent comp = new AssessmentComponent();
            comp.setAssessment(saved);
            comp.setComponentType("WRITTEN");
            comp.setWeightage(1.0);
            comp.setMaxMarks(assessment.getMaxMarks() > 0 ? assessment.getMaxMarks() : 20.0);
            componentRepository.save(comp);
            saved.setComponents(Collections.singletonList(comp));
        }

        return saved;
    }

    // Save Marks Draft
    @Transactional
    public List<AssessmentMark> saveMarks(Long assessmentId, Long componentId, List<AssessmentMark> marks,
            String facultyId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found."));
        AssessmentComponent component = componentRepository.findById(componentId)
                .orElseThrow(() -> new IllegalArgumentException("Component not found."));

        if (!assessment.getFacultyId().equalsIgnoreCase(facultyId)) {
            throw new IllegalStateException("Only assigned faculty can input marks.");
        }

        if ("DEAN_SUBMITTED".equalsIgnoreCase(assessment.getStatus())) {
            throw new IllegalStateException("Assessment is locked. Cannot edit marks.");
        }

        List<AssessmentMark> savedMarks = new ArrayList<>();
        for (AssessmentMark mark : marks) {
            // Validate limits
            if (mark.getMarksObtained() < 0 || mark.getMarksObtained() > component.getMaxMarks()) {
                throw new IllegalArgumentException("Marks must be between 0 and " + component.getMaxMarks()
                        + " (Student: " + mark.getStudent().getId() + ")");
            }

            Optional<AssessmentMark> existing = markRepository.findByAssessmentIdAndComponentIdAndStudentId(
                    assessmentId, componentId, mark.getStudent().getId());

            AssessmentMark markToSave;
            if (existing.isPresent()) {
                markToSave = existing.get();
                markToSave.setMarksObtained(mark.getMarksObtained());
                markToSave.setUpdatedAt(LocalDateTime.now());
            } else {
                markToSave = mark;
                markToSave.setAssessment(assessment);
                markToSave.setComponent(component);
                markToSave.setEnteredBy(facultyId);
                markToSave.setEnteredAt(LocalDateTime.now());
                markToSave.setUpdatedAt(LocalDateTime.now());
            }
            savedMarks.add(markRepository.save(markToSave));
        }

        return savedMarks;
    }

    // Submit Assessment by Faculty
    @Transactional
    public Assessment submitAssessment(Long assessmentId, String facultyId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found."));

        if (!assessment.getFacultyId().equalsIgnoreCase(facultyId)) {
            throw new IllegalStateException("Only assigned teaching faculty can submit marks.");
        }

        assessment.setStatus("SUBMITTED");
        AssessmentWorkflow workflow = workflowRepository.findByAssessmentId(assessmentId)
                .orElseGet(() -> {
                    AssessmentWorkflow wf = new AssessmentWorkflow();
                    wf.setAssessment(assessment);
                    return wf;
                });
        workflow.setFacultyStatus("SUBMITTED");
        workflow.setSubmittedAt(LocalDateTime.now());
        workflowRepository.save(workflow);

        return assessmentRepository.save(assessment);
    }

    // Verify by Class Incharge
    @Transactional
    public Assessment verifyClassIncharge(Long assessmentId, boolean checkSuccess) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found."));

        AssessmentWorkflow workflow = workflowRepository.findByAssessmentId(assessmentId)
                .orElseThrow(() -> new IllegalStateException("Workflow track not found."));

        if (checkSuccess) {
            assessment.setStatus("INCHARGE_VERIFIED");
            workflow.setClassInchargeStatus("VERIFIED");
            workflow.setVerifiedAt(LocalDateTime.now());
        } else {
            // Reopen workflow to Faculty
            assessment.setStatus("DRAFT");
            workflow.setFacultyStatus("DRAFT");
            workflow.setClassInchargeStatus("REOPENED");
        }
        workflowRepository.save(workflow);
        return assessmentRepository.save(assessment);
    }

    // Verify / Approve by HOD
    @Transactional
    public Assessment verifyHod(Long assessmentId, boolean approve, String comment) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found."));

        AssessmentWorkflow workflow = workflowRepository.findByAssessmentId(assessmentId)
                .orElseThrow(() -> new IllegalStateException("Workflow track not found."));

        if (approve) {
            assessment.setStatus("HOD_APPROVED");
            workflow.setHodStatus("APPROVED");
            workflow.setApprovedAt(LocalDateTime.now());
        } else {
            assessment.setStatus("DRAFT");
            workflow.setFacultyStatus("DRAFT");
            workflow.setClassInchargeStatus("PENDING");
            workflow.setHodStatus("REJECTED");
            workflow.setHodComments(comment);
        }
        workflowRepository.save(workflow);
        return assessmentRepository.save(assessment);
    }

    // Submit to Dean (Official Lock)
    @Transactional
    public Assessment submitToDean(Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found."));

        AssessmentWorkflow workflow = workflowRepository.findByAssessmentId(assessmentId)
                .orElseThrow(() -> new IllegalStateException("Workflow track not found."));

        assessment.setStatus("DEAN_SUBMITTED");
        workflow.setDeanStatus("LOCKED");
        workflow.setLockedAt(LocalDateTime.now());

        workflowRepository.save(workflow);
        return assessmentRepository.save(assessment);
    }

    // GET marks details for viewing
    public List<AssessmentMark> getMarksForAssessment(Long assessmentId) {
        return markRepository.findByAssessmentId(assessmentId);
    }

    // GET consolidated class reports for IAT marks
    public Map<String, Object> getConsolidatedMarksReport(String department, String semester, String section) {
        List<Student> students = studentRepository.findByDepartmentAndSemesterAndSection(department, semester, section);
        List<Assessment> iatList = assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(department,
                semester, section, "IAT");

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> studentReports = new ArrayList<>();

        for (Student student : students) {
            Map<String, Object> stuRpt = new HashMap<>();
            stuRpt.put("studentId", student.getId());
            stuRpt.put("studentName", student.getName());
            stuRpt.put("rollNumber", student.getRollNumber());

            double iat1TotalWeightMark = 0.0;
            double iat2TotalWeightMark = 0.0;
            boolean iat1Found = false;
            boolean iat2Found = false;

            for (Assessment iat : iatList) {
                boolean isIat1 = iat.getName().equalsIgnoreCase("IAT 1");
                boolean isIat2 = iat.getName().equalsIgnoreCase("IAT 2");

                if (!isIat1 && !isIat2)
                    continue;

                double weightedSum = 0.0;
                List<AssessmentMark> marks = markRepository.findByAssessmentIdAndStudentId(iat.getId(),
                        student.getId());

                for (AssessmentMark mark : marks) {
                    AssessmentComponent comp = mark.getComponent();
                    double componentPercent = mark.getMarksObtained() / comp.getMaxMarks();
                    weightedSum += componentPercent * comp.getMaxMarks() * comp.getWeightage();
                }

                if (isIat1) {
                    stuRpt.put("iat1Marks", getComponentMarksMap(marks));
                    stuRpt.put("iat1Internal", weightedSum);
                    iat1TotalWeightMark = weightedSum;
                    iat1Found = true;
                } else {
                    stuRpt.put("iat2Marks", getComponentMarksMap(marks));
                    stuRpt.put("iat2Internal", weightedSum);
                    iat2TotalWeightMark = weightedSum;
                    iat2Found = true;
                }
            }

            double consolidated = 0.0;
            if (iat1Found && iat2Found) {
                consolidated = (iat1TotalWeightMark + iat2TotalWeightMark) / 2.0;
            } else if (iat1Found) {
                consolidated = iat1TotalWeightMark;
            } else if (iat2Found) {
                consolidated = iat2TotalWeightMark;
            }

            stuRpt.put("consolidatedInternal", consolidated);
            stuRpt.put("finalInternal", Math.round(consolidated)); // Round to neat integers

            studentReports.add(stuRpt);
        }

        response.put("semester", semester);
        response.put("section", section);
        response.put("department", department);
        response.put("students", studentReports);

        return response;
    }

    private Map<String, Double> getComponentMarksMap(List<AssessmentMark> marks) {
        Map<String, Double> compMap = new HashMap<>();
        for (AssessmentMark m : marks) {
            compMap.put(m.getComponent().getComponentType(), m.getMarksObtained());
        }
        return compMap;
    }
}
