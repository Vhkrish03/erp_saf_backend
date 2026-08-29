package erp_backend.progress.service;

import erp_backend.entity.*;
import erp_backend.examcell.entity.ExamCellResult;
import erp_backend.examcell.repository.ExamCellResultRepository;
import erp_backend.progress.entity.*;
import erp_backend.progress.repository.*;
import erp_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redesigned ProgressCardService.
 *
 * Core rules:
 * - Internal assessment marks shown to students ONLY when Assessment.status =
 * "DEAN_SUBMITTED"
 * - Semester exam results shown to students ONLY when ExamCellResult.status =
 * "PUBLISHED"
 * - Students see own data only (enforce studentId scoping at controller + auth
 * layer)
 * - Performance categories come from PerformanceThresholdConfig (DB) — never
 * hard-coded
 * - CGPA computed from PUBLISHED ExamCellResult records only
 */
@Service
public class ProgressCardService {

        private final StudentRepository studentRepository;
        private final AssessmentRepository assessmentRepository;
        private final AssessmentMarkRepository markRepository;
        private final SubjectRepository subjectRepository;
        private final ExamCellResultRepository examCellResultRepository;
        private final InternalMarkRepository internalMarkRepository;
        private final InternalMarkConfigRepository internalMarkConfigRepository;
        private final PerformanceRemarkRepository remarkRepository;
        private final PerformanceThresholdConfigRepository thresholdRepository;

        public ProgressCardService(
                        StudentRepository studentRepository,
                        AssessmentRepository assessmentRepository,
                        AssessmentMarkRepository markRepository,
                        SubjectRepository subjectRepository,
                        ExamCellResultRepository examCellResultRepository,
                        InternalMarkRepository internalMarkRepository,
                        InternalMarkConfigRepository internalMarkConfigRepository,
                        PerformanceRemarkRepository remarkRepository,
                        PerformanceThresholdConfigRepository thresholdRepository) {
                this.studentRepository = studentRepository;
                this.assessmentRepository = assessmentRepository;
                this.markRepository = markRepository;
                this.subjectRepository = subjectRepository;
                this.examCellResultRepository = examCellResultRepository;
                this.internalMarkRepository = internalMarkRepository;
                this.internalMarkConfigRepository = internalMarkConfigRepository;
                this.remarkRepository = remarkRepository;
                this.thresholdRepository = thresholdRepository;
        }

        // ─────────────────────────────────────────────────────────────────────────
        // STUDENT VIEW — FINALIZED internal marks + PUBLISHED semester results only
        // ─────────────────────────────────────────────────────────────────────────

        public Map<String, Object> getProgressCard(String studentId, String semester) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

                String academicYear = resolveAcademicYear();
                Map<String, Object> card = new LinkedHashMap<>();

                card.put("studentInfo", buildStudentInfo(student));
                card.put("verificationTrail", buildVerificationTrail(student, semester));

                // Official semester exam result — PUBLISHED only
                Optional<ExamCellResult> publishedResult = examCellResultRepository
                                .findPublishedByStudentIdAndSemester(studentId, semester);
                card.put("semesterResult", buildSemesterResultSection(publishedResult));

                // Internal assessment — FINALIZED (DEAN_SUBMITTED) only for student view
                card.put("internalPerformance",
                                buildInternalPerformance(student, semester, academicYear, true));

                card.put("cgpa", calculateCgpa(studentId));
                card.put("overallProgress", buildOverallProgress(studentId));
                card.put("performanceOverview",
                                buildPerformanceOverview(studentId, semester, academicYear));
                card.put("remarks", buildRemarksSection(studentId, semester, academicYear));

                return card;
        }

        // ─────────────────────────────────────────────────────────────────────────
        // STAFF VIEW — all statuses visible + workflow context
        // ─────────────────────────────────────────────────────────────────────────

        public Map<String, Object> getProgressCardForStaff(String studentId, String semester) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

                String academicYear = resolveAcademicYear();
                Map<String, Object> card = new LinkedHashMap<>();

                card.put("studentInfo", buildStudentInfo(student));
                card.put("verificationTrail", buildVerificationTrail(student, semester));

                // Show any result, not just published
                Optional<ExamCellResult> anyResult = examCellResultRepository
                                .findByStudentIdAndSemesterNameAndAcademicYear(
                                                studentId, semester, academicYear);
                card.put("semesterResult", buildSemesterResultSection(anyResult));

                // All assessment data, all statuses
                card.put("internalPerformance",
                                buildInternalPerformance(student, semester, academicYear, false));

                card.put("cgpa", calculateCgpa(studentId));
                card.put("overallProgress", buildOverallProgress(studentId));
                card.put("performanceOverview",
                                buildPerformanceOverview(studentId, semester, academicYear));
                card.put("remarks", buildRemarksSection(studentId, semester, academicYear));

                return card;
        }

        // ─────────────────────────────────────────────────────────────────────────
        // CLASS PROGRESS — Incharge / HOD dashboard
        // ─────────────────────────────────────────────────────────────────────────

        public List<Map<String, Object>> getClassProgress(
                        String department, String semester, String section, String academicYear) {

                List<Student> students = studentRepository.findByDepartmentAndSemesterAndSection(department, semester,
                                section);

                List<Assessment> weeklyAll = assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(
                                department, semester, section, "WEEKLY");
                List<Assessment> iatAll = assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(
                                department, semester, section, "IAT");

                return students.stream().map(student -> {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("studentId", student.getId());
                        row.put("studentName", student.getName());
                        row.put("rollNumber", student.getRollNumber());

                        List<InternalMark> internalMarks = internalMarkRepository
                                        .findByStudentIdAndSemesterAndAcademicYear(
                                                        student.getId(), semester, academicYear);
                        row.put("internalMarks", internalMarks);

                        long weeklyFinalized = weeklyAll.stream()
                                        .filter(a -> "DEAN_SUBMITTED".equalsIgnoreCase(a.getStatus())).count();
                        long iatFinalized = iatAll.stream()
                                        .filter(a -> "DEAN_SUBMITTED".equalsIgnoreCase(a.getStatus())).count();

                        row.put("weeklyTestCount", weeklyAll.size());
                        row.put("weeklyFinalized", weeklyFinalized);
                        row.put("iatCount", iatAll.size());
                        row.put("iatFinalized", iatFinalized);

                        Optional<ExamCellResult> res = examCellResultRepository.findPublishedByStudentIdAndSemester(
                                        student.getId(), semester);
                        row.put("examResultStatus",
                                        res.map(ExamCellResult::getStatus).orElse("PENDING"));
                        row.put("sgpa", res.map(ExamCellResult::getSgpa).orElse(0.0));

                        return row;
                }).collect(Collectors.toList());
        }

        // ─────────────────────────────────────────────────────────────────────────
        // REMARKS
        // ─────────────────────────────────────────────────────────────────────────

        @Transactional
        public PerformanceRemark addRemark(String studentId, String semester, String academicYear,
                        String remarkBy, String remarkByRole,
                        String remarkByName, String remarkText) {
                PerformanceRemark r = new PerformanceRemark();
                r.setStudentId(studentId);
                r.setSemester(semester);
                r.setAcademicYear(academicYear);
                r.setRemarkBy(remarkBy);
                r.setRemarkByRole(remarkByRole);
                r.setRemarkByName(remarkByName);
                r.setRemarkText(remarkText);
                return remarkRepository.save(r);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // INTERNAL MARK CALCULATION (DB-config driven, no hard-coded weightage)
        // ─────────────────────────────────────────────────────────────────────────

        @Transactional
        public InternalMark calculateAndStoreInternalMark(
                        String studentId, Long subjectId, String semester, String academicYear) {

                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
                Subject subject = subjectRepository.findById(subjectId)
                                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));

                InternalMarkConfig config = internalMarkConfigRepository
                                .findByDepartmentAndSemesterAndAcademicYearAndIsActive(
                                                student.getDepartment(), semester, academicYear, true)
                                .orElse(null);

                // FINALIZED weekly assessments only
                List<Assessment> weeklyList = assessmentRepository
                                .findByDepartmentAndSemesterAndSectionAndTypeAndAcademicYear(
                                                student.getDepartment(), semester, student.getSection(), "WEEKLY",
                                                academicYear)
                                .stream()
                                .filter(a -> "DEAN_SUBMITTED".equalsIgnoreCase(a.getStatus()))
                                .filter(a -> a.getSubject() != null &&
                                                a.getSubject().getId().equals(subjectId))
                                .collect(Collectors.toList());

                List<Double> weeklyScores = weeklyList.stream()
                                .flatMap(a -> markRepository.findByAssessmentIdAndStudentId(a.getId(), studentId)
                                                .stream())
                                .map(AssessmentMark::getMarksObtained)
                                .collect(Collectors.toList());

                double weeklyAvg = 0.0;
                if (!weeklyScores.isEmpty()) {
                        int bestOf = (config != null && config.getBestOfWeeklyTests() > 0)
                                        ? config.getBestOfWeeklyTests()
                                        : weeklyScores.size();
                        weeklyScores.sort(Comparator.reverseOrder());
                        List<Double> best = weeklyScores.subList(0, Math.min(bestOf, weeklyScores.size()));
                        weeklyAvg = best.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                }

                double iat1Total = computeIatWeighted(
                                studentId, subjectId, "iat 1", student, semester, academicYear);
                double iat2Total = computeIatWeighted(
                                studentId, subjectId, "iat 2", student, semester, academicYear);

                double wt = config != null ? config.getWeeklyTestWeightage() : 0.20;
                double w1 = config != null ? config.getIat1Weightage() : 0.40;
                double w2 = config != null ? config.getIat2Weightage() : 0.40;
                double max = config != null ? config.getTotalInternalMax() : 50.0;
                double iatMax = config != null ? config.getIatMaxMarks() : 50.0;

                double calculated = (weeklyAvg * wt)
                                + (iatMax > 0 ? iat1Total / iatMax : 0) * w1 * max
                                + (iatMax > 0 ? iat2Total / iatMax : 0) * w2 * max;
                double finalInternal = Math.min(Math.round(calculated), max);

                InternalMark im = internalMarkRepository
                                .findByStudentIdAndSubjectIdAndSemesterAndAcademicYear(
                                                studentId, subjectId, semester, academicYear)
                                .orElse(new InternalMark());

                im.setStudentId(studentId);
                im.setSubjectId(subjectId);
                im.setSubjectCode(subject.getCode());
                im.setSubjectName(subject.getName());
                im.setAcademicYear(academicYear);
                im.setSemester(semester);
                im.setDepartment(student.getDepartment());
                im.setSection(student.getSection());
                im.setWeeklyTestAverage(weeklyAvg);
                im.setWeeklyTestBestAverage(weeklyAvg);
                im.setIat1Total(iat1Total);
                im.setIat2Total(iat2Total);
                im.setCalculatedInternal(calculated);
                im.setFinalInternal(finalInternal);
                im.setStatus("CALCULATED");
                im.setCalculatedAt(LocalDateTime.now());

                return internalMarkRepository.save(im);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // PRIVATE HELPERS
        // ─────────────────────────────────────────────────────────────────────────

        private Map<String, Object> buildStudentInfo(Student s) {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("id", s.getId());
                info.put("name", s.getName());
                info.put("rollNumber", s.getRollNumber());
                info.put("department", s.getDepartment());
                info.put("section", s.getSection());
                info.put("year", s.getYear());
                info.put("semester", s.getSemester());
                info.put("email", s.getEmail());
                info.put("phone", s.getPhone());
                return info;
        }

        private Map<String, Object> buildVerificationTrail(Student student, String semester) {
                String dept = student.getDepartment();
                String section = student.getSection();

                List<Assessment> all = new ArrayList<>();
                all.addAll(assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(
                                dept, semester, section, "WEEKLY"));
                all.addAll(assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(
                                dept, semester, section, "IAT"));

                long total = all.size();
                long submitted = all.stream().filter(a -> !"DRAFT".equalsIgnoreCase(a.getStatus())).count();
                long inchargeVerified = all.stream()
                                .filter(a -> List.of("INCHARGE_VERIFIED", "HOD_APPROVED", "DEAN_SUBMITTED")
                                                .contains(a.getStatus()))
                                .count();
                long hodApproved = all.stream()
                                .filter(a -> List.of("HOD_APPROVED", "DEAN_SUBMITTED").contains(a.getStatus())).count();
                long deanFinalized = all.stream()
                                .filter(a -> "DEAN_SUBMITTED".equalsIgnoreCase(a.getStatus())).count();

                String overallStatus;
                if (total == 0)
                        overallStatus = "NO_DATA";
                else if (deanFinalized == total)
                        overallStatus = "FINALIZED";
                else if (hodApproved > 0)
                        overallStatus = "HOD_APPROVED";
                else if (inchargeVerified > 0)
                        overallStatus = "INCHARGE_VERIFIED";
                else if (submitted > 0)
                        overallStatus = "SUBMITTED";
                else
                        overallStatus = "DRAFT";

                Map<String, Object> trail = new LinkedHashMap<>();
                trail.put("totalAssessments", total);
                trail.put("facultySubmitted", submitted);
                trail.put("classInchargeVerified", inchargeVerified);
                trail.put("hodApproved", hodApproved);
                trail.put("deanFinalized", deanFinalized);
                trail.put("overallInternalStatus", overallStatus);
                return trail;
        }

        private Map<String, Object> buildSemesterResultSection(Optional<ExamCellResult> opt) {
                Map<String, Object> sec = new LinkedHashMap<>();
                if (opt.isPresent()) {
                        ExamCellResult r = opt.get();
                        sec.put("resultId", r.getId());
                        sec.put("semesterName", r.getSemesterName());
                        sec.put("sgpa", r.getSgpa());
                        sec.put("status", r.getStatus());
                        sec.put("examination", r.getExamination());
                        sec.put("examSession", r.getExamSession());
                        sec.put("publishedAt", r.getPublishedAt());
                        sec.put("subjects", r.getSubjects());
                } else {
                        sec.put("resultId", null);
                        sec.put("semesterName", null);
                        sec.put("sgpa", 0.0);
                        sec.put("status", "NOT_PUBLISHED");
                        sec.put("examination", null);
                        sec.put("examSession", null);
                        sec.put("publishedAt", null);
                        sec.put("subjects", Collections.emptyList());
                }
                return sec;
        }

        private List<Map<String, Object>> buildInternalPerformance(
                        Student student, String semester, String academicYear, boolean finalizedOnly) {

                String dept = student.getDepartment();
                String section = student.getSection();
                String sid = student.getId();

                List<Assessment> weeklyList;
                List<Assessment> iatList;

                if (finalizedOnly) {
                        weeklyList = assessmentRepository
                                        .findByDepartmentAndSemesterAndSectionAndTypeAndAcademicYear(
                                                        dept, semester, section, "WEEKLY", academicYear)
                                        .stream().filter(a -> "DEAN_SUBMITTED".equalsIgnoreCase(a.getStatus()))
                                        .collect(Collectors.toList());
                        iatList = assessmentRepository
                                        .findByDepartmentAndSemesterAndSectionAndTypeAndAcademicYear(
                                                        dept, semester, section, "IAT", academicYear)
                                        .stream().filter(a -> "DEAN_SUBMITTED".equalsIgnoreCase(a.getStatus()))
                                        .collect(Collectors.toList());
                } else {
                        weeklyList = assessmentRepository
                                        .findByDepartmentAndSemesterAndSectionAndType(dept, semester, section,
                                                        "WEEKLY");
                        iatList = assessmentRepository
                                        .findByDepartmentAndSemesterAndSectionAndType(dept, semester, section, "IAT");
                }

                Set<Subject> subjects = new LinkedHashSet<>();
                weeklyList.forEach(a -> {
                        if (a.getSubject() != null)
                                subjects.add(a.getSubject());
                });
                iatList.forEach(a -> {
                        if (a.getSubject() != null)
                                subjects.add(a.getSubject());
                });

                // Fallback: subjects from DB
                if (subjects.isEmpty()) {
                        try {
                                int semNum = Integer.parseInt(semester.replaceAll("[^0-9]", ""));
                                subjectRepository.findAll().stream()
                                                .filter(s -> dept.equalsIgnoreCase(s.getDepartment())
                                                                && s.getSemester() == semNum)
                                                .forEach(subjects::add);
                        } catch (Exception ignored) {
                        }
                }

                List<Map<String, Object>> result = new ArrayList<>();
                for (Subject sub : subjects) {
                        result.add(buildSubjectInternalMap(sid, sub, weeklyList, iatList, semester, academicYear));
                }
                return result;
        }

        private Map<String, Object> buildSubjectInternalMap(
                        String studentId, Subject subject,
                        List<Assessment> weeklyList, List<Assessment> iatList,
                        String semester, String academicYear) {

                Map<String, Object> m = new LinkedHashMap<>();
                m.put("subjectCode", subject.getCode());
                m.put("subjectName", subject.getName());
                m.put("credits", subject.getCredits());

                // Weekly tests
                Map<String, Object> dtMarks = new LinkedHashMap<>();
                for (int i = 1; i <= 6; i++)
                        dtMarks.put("Daily Test " + i, null);
                String weeklyStatus = "DRAFT";
                for (Assessment w : weeklyList) {
                        if (w.getSubject() != null &&
                                        w.getSubject().getCode().equalsIgnoreCase(subject.getCode())) {
                                List<AssessmentMark> marks = markRepository.findByAssessmentIdAndStudentId(w.getId(),
                                                studentId);
                                if (!marks.isEmpty())
                                        dtMarks.put(w.getName().trim(), marks.get(0).getMarksObtained());
                                weeklyStatus = w.getStatus();
                        }
                }
                m.put("dailyTests", dtMarks);
                m.put("weeklyTestStatus", weeklyStatus);

                // IAT marks
                Map<String, Object> iat1Map = defaultIatMap();
                Map<String, Object> iat2Map = defaultIatMap();
                double iat1W = 0, iat2W = 0;
                String iat1Status = "DRAFT", iat2Status = "DRAFT";

                for (Assessment iat : iatList) {
                        if (iat.getSubject() == null ||
                                        !iat.getSubject().getCode().equalsIgnoreCase(subject.getCode()))
                                continue;

                        String nameLower = iat.getName().toLowerCase();
                        boolean isIat1 = nameLower.contains("iat 1") || nameLower.contains("iat-1");
                        boolean isIat2 = nameLower.contains("iat 2") || nameLower.contains("iat-2");

                        List<AssessmentMark> marks = markRepository.findByAssessmentIdAndStudentId(iat.getId(),
                                        studentId);
                        double ws = 0;
                        Map<String, Object> compMap = defaultIatMap();
                        for (AssessmentMark mark : marks) {
                                AssessmentComponent c = mark.getComponent();
                                ws += c.getMaxMarks() > 0
                                                ? (mark.getMarksObtained() / c.getMaxMarks()) * c.getMaxMarks()
                                                                * c.getWeightage()
                                                : 0;
                                compMap.put(c.getComponentType().toUpperCase(), mark.getMarksObtained());
                        }
                        if (isIat1) {
                                iat1Map = compMap;
                                iat1W = ws;
                                iat1Status = iat.getStatus();
                        }
                        if (isIat2) {
                                iat2Map = compMap;
                                iat2W = ws;
                                iat2Status = iat.getStatus();
                        }
                }

                m.put("iat1", iat1Map);
                m.put("iat2", iat2Map);
                m.put("iat1Internal", iat1W);
                m.put("iat2Internal", iat2W);
                m.put("iat1Status", iat1Status);
                m.put("iat2Status", iat2Status);

                double consolidated = (iat1W > 0 && iat2W > 0)
                                ? (iat1W + iat2W) / 2.0
                                : Math.max(iat1W, iat2W);
                m.put("consolidatedInternal", consolidated);
                m.put("finalInternal", (long) Math.round(consolidated));

                // Stored InternalMark record
                internalMarkRepository
                                .findByStudentIdAndSubjectIdAndSemesterAndAcademicYear(
                                                studentId, subject.getId(), semester, academicYear)
                                .ifPresent(im -> m.put("storedInternalMark", im));

                return m;
        }

        private double computeIatWeighted(String studentId, Long subjectId, String iatNameKeyword,
                        Student student, String semester, String academicYear) {
                List<Assessment> list = assessmentRepository
                                .findByDepartmentAndSemesterAndSectionAndType(
                                                student.getDepartment(), semester, student.getSection(), "IAT");

                return list.stream()
                                .filter(a -> a.getSubject() != null && a.getSubject().getId().equals(subjectId))
                                .filter(a -> a.getName().toLowerCase().contains(iatNameKeyword))
                                .mapToDouble(iat -> markRepository
                                                .findByAssessmentIdAndStudentId(iat.getId(), studentId)
                                                .stream().mapToDouble(mk -> {
                                                        AssessmentComponent c = mk.getComponent();
                                                        return c.getMaxMarks() > 0
                                                                        ? (mk.getMarksObtained() / c.getMaxMarks())
                                                                                        * c.getMaxMarks()
                                                                                        * c.getWeightage()
                                                                        : 0;
                                                }).sum())
                                .sum();
        }

        private double calculateCgpa(String studentId) {
                List<ExamCellResult> published = examCellResultRepository.findPublishedByStudentId(studentId);
                if (published.isEmpty())
                        return 0.0;
                return published.stream().mapToDouble(ExamCellResult::getSgpa).average().orElse(0.0);
        }

        private List<Map<String, Object>> buildOverallProgress(String studentId) {
                return examCellResultRepository.findPublishedByStudentId(studentId).stream()
                                .sorted(Comparator.comparing(ExamCellResult::getSemesterName))
                                .map(r -> {
                                        Map<String, Object> sm = new LinkedHashMap<>();
                                        sm.put("semesterName", r.getSemesterName());
                                        sm.put("sgpa", r.getSgpa());
                                        sm.put("examSession", r.getExamSession());
                                        sm.put("publishedAt", r.getPublishedAt());
                                        return sm;
                                }).collect(Collectors.toList());
        }

        private Map<String, Object> buildPerformanceOverview(
                        String studentId, String semester, String academicYear) {

                List<InternalMark> marks = internalMarkRepository
                                .findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear);

                double avgInternal = marks.stream()
                                .filter(im -> im.getFinalInternal() != null)
                                .mapToDouble(InternalMark::getFinalInternal)
                                .average().orElse(0.0);

                // Load configurable thresholds — ordered by sortOrder ascending
                List<PerformanceThresholdConfig> thresholds = thresholdRepository
                                .findByIsActiveOrderBySortOrderAsc(true);

                String label = "INSUFFICIENT_DATA";
                String display = "Insufficient Data";
                for (PerformanceThresholdConfig tc : thresholds) {
                        if (avgInternal >= tc.getMinAverage() && avgInternal <= tc.getMaxAverage()) {
                                label = tc.getLabel();
                                display = tc.getDisplayName();
                                break;
                        }
                }

                Map<String, Object> ov = new LinkedHashMap<>();
                ov.put("averageInternal", Math.round(avgInternal * 100.0) / 100.0);
                ov.put("performanceLabel", label);
                ov.put("performanceDisplay", display);
                ov.put("subjectCount", marks.size());
                return ov;
        }

        private List<Map<String, Object>> buildRemarksSection(
                        String studentId, String semester, String academicYear) {
                return remarkRepository
                                .findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear)
                                .stream().map(r -> {
                                        Map<String, Object> rm = new LinkedHashMap<>();
                                        rm.put("remarkBy", r.getRemarkBy());
                                        rm.put("remarkByName", r.getRemarkByName());
                                        rm.put("remarkByRole", r.getRemarkByRole());
                                        rm.put("remarkText", r.getRemarkText());
                                        rm.put("createdAt", r.getCreatedAt());
                                        return rm;
                                }).collect(Collectors.toList());
        }

        private String resolveAcademicYear() {
                LocalDate today = LocalDate.now();
                int year = today.getYear();
                int month = today.getMonthValue();
                // Academic year starts June
                if (month >= 6) {
                        return year + "-" + String.valueOf(year + 1).substring(2);
                } else {
                        return (year - 1) + "-" + String.valueOf(year).substring(2);
                }
        }

        private Map<String, Object> defaultIatMap() {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("WRITTEN", null);
                m.put("ASSIGNMENT", null);
                m.put("SEMINAR", null);
                m.put("QUIZ", null);
                return m;
        }
}
