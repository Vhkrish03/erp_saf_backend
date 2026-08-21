package erp_backend.progress.service;

import java.util.*;
import org.springframework.stereotype.Service;
import erp_backend.entity.*;
import erp_backend.repository.*;
import erp_backend.service.SemesterResultService;

@Service
public class ProgressCardService {

    private final StudentRepository studentRepository;
    private final SemesterResultRepository semesterResultRepository;
    private final AssessmentRepository assessmentRepository;
    private final AssessmentMarkRepository markRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterResultService semesterResultService;

    public ProgressCardService(StudentRepository studentRepository,
            SemesterResultRepository semesterResultRepository,
            AssessmentRepository assessmentRepository,
            AssessmentMarkRepository markRepository,
            SubjectRepository subjectRepository,
            SemesterResultService semesterResultService) {
        this.studentRepository = studentRepository;
        this.semesterResultRepository = semesterResultRepository;
        this.assessmentRepository = assessmentRepository;
        this.markRepository = markRepository;
        this.subjectRepository = subjectRepository;
        this.semesterResultService = semesterResultService;
    }

    public Map<String, Object> getProgressCard(String studentId, String semester) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        Map<String, Object> progressCard = new HashMap<>();

        // 1. Student Information
        Map<String, Object> studentInfo = new HashMap<>();
        studentInfo.put("collegeName", "AROX Engineering College");
        studentInfo.put("name", student.getName());
        studentInfo.put("rollNumber", student.getRollNumber());
        studentInfo.put("department", student.getDepartment());
        studentInfo.put("section", student.getSection());
        studentInfo.put("year", student.getYear());
        studentInfo.put("semester", student.getSemester());
        studentInfo.put("email", student.getEmail());
        studentInfo.put("phone", student.getPhone());
        progressCard.put("studentInfo", studentInfo);

        // 2. Official Semester Result
        Optional<SemesterResult> resultOpt = semesterResultRepository.findByStudent_IdAndSemesterName(studentId,
                semester);
        Map<String, Object> examResultMap = new HashMap<>();
        if (resultOpt.isPresent()) {
            SemesterResult sr = resultOpt.get();
            examResultMap.put("semesterName", sr.getSemesterName());
            examResultMap.put("sgpa", sr.getSgpa());
            examResultMap.put("status", sr.getStatus());
            examResultMap.put("examination", sr.getExamination());
            examResultMap.put("examSession", sr.getExamSession());
            examResultMap.put("publishedAt", sr.getPublishedAt());
            examResultMap.put("subjects", sr.getResults());
        } else {
            examResultMap.put("semesterName", semester);
            examResultMap.put("sgpa", 0.0);
            examResultMap.put("status", "UNPUBLISHED");
            examResultMap.put("examination", null);
            examResultMap.put("examSession", null);
            examResultMap.put("publishedAt", null);
            examResultMap.put("subjects", new ArrayList<>());
        }
        progressCard.put("semesterResult", examResultMap);

        // 3. CGPA
        double currentCgpa = semesterResultService.calculateCgpa(studentId);
        progressCard.put("cgpa", currentCgpa);

        // 4. Internal Assessment Performance
        List<Assessment> weeklyList = assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(
                student.getDepartment(), semester, student.getSection(), "WEEKLY");
        List<Assessment> iatList = assessmentRepository.findByDepartmentAndSemesterAndSectionAndType(
                student.getDepartment(), semester, student.getSection(), "IAT");

        Set<Subject> subjects = new HashSet<>();
        for (Assessment asm : weeklyList) {
            if (asm.getSubject() != null)
                subjects.add(asm.getSubject());
        }
        for (Assessment asm : iatList) {
            if (asm.getSubject() != null)
                subjects.add(asm.getSubject());
        }

        // Fallback: If no assessments exist in DB, seek subjects under department &
        // semester
        if (subjects.isEmpty()) {
            try {
                int semNum = Integer.parseInt(semester.replaceAll("[^0-9]", ""));
                List<Subject> allSubs = subjectRepository.findAll();
                for (Subject s : allSubs) {
                    if (s.getDepartment() != null && s.getDepartment().equalsIgnoreCase(student.getDepartment())
                            && s.getSemester() == semNum) {
                        subjects.add(s);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        List<Map<String, Object>> subjectMarksList = new ArrayList<>();
        for (Subject subject : subjects) {
            Map<String, Object> subMap = new HashMap<>();
            subMap.put("subjectCode", subject.getCode());
            subMap.put("subjectName", subject.getName());
            subMap.put("credits", subject.getCredits());

            // 1. Weekly/Daily Test marks (Daily Test 1 to 6)
            Map<String, Double> dtMarks = new HashMap<>();
            for (int i = 1; i <= 6; i++) {
                dtMarks.put("Daily Test " + i, null);
            }
            for (Assessment weekly : weeklyList) {
                if (weekly.getSubject() != null
                        && weekly.getSubject().getCode().equalsIgnoreCase(subject.getCode())) {
                    List<AssessmentMark> marks = markRepository.findByAssessmentIdAndStudentId(weekly.getId(),
                            studentId);
                    if (!marks.isEmpty()) {
                        dtMarks.put(weekly.getName().trim(), marks.get(0).getMarksObtained());
                    }
                }
            }
            subMap.put("dailyTests", dtMarks);

            // 2. IAT 1 & 2 component marks
            Map<String, Object> iat1Marks = new HashMap<>();
            Map<String, Object> iat2Marks = new HashMap<>();
            double iat1Weighted = 0.0;
            double iat2Weighted = 0.0;
            boolean iat1Found = false;
            boolean iat2Found = false;

            // initialize defaults
            for (String compName : Arrays.asList("WRITTEN", "ASSIGNMENT", "SEMINAR", "QUIZ")) {
                iat1Marks.put(compName, null);
                iat2Marks.put(compName, null);
            }

            for (Assessment iat : iatList) {
                if (iat.getSubject() != null && iat.getSubject().getCode().equalsIgnoreCase(subject.getCode())) {
                    boolean isIat1 = iat.getName().equalsIgnoreCase("IAT 1") || iat.getName().equalsIgnoreCase("IAT-1");
                    boolean isIat2 = iat.getName().equalsIgnoreCase("IAT 2") || iat.getName().equalsIgnoreCase("IAT-2");

                    if (!isIat1 && !isIat2)
                        continue;

                    List<AssessmentMark> marks = markRepository.findByAssessmentIdAndStudentId(iat.getId(), studentId);
                    double weightedSum = 0.0;
                    Map<String, Object> compMap = new HashMap<>();

                    for (AssessmentMark mark : marks) {
                        AssessmentComponent comp = mark.getComponent();
                        double componentPercent = comp.getMaxMarks() > 0 ? mark.getMarksObtained() / comp.getMaxMarks()
                                : 0.0;
                        weightedSum += componentPercent * comp.getMaxMarks() * comp.getWeightage();
                        compMap.put(comp.getComponentType().toUpperCase(), mark.getMarksObtained());
                    }

                    if (isIat1) {
                        // Blend entries on top of initialized
                        for (Map.Entry<String, Object> entry : compMap.entrySet()) {
                            iat1Marks.put(entry.getKey(), entry.getValue());
                        }
                        iat1Weighted = weightedSum;
                        iat1Found = true;
                    } else {
                        for (Map.Entry<String, Object> entry : compMap.entrySet()) {
                            iat2Marks.put(entry.getKey(), entry.getValue());
                        }
                        iat2Weighted = weightedSum;
                        iat2Found = true;
                    }
                }
            }
            subMap.put("iat1", iat1Marks);
            subMap.put("iat2", iat2Marks);
            subMap.put("iat1Internal", iat1Weighted);
            subMap.put("iat2Internal", iat2Weighted);

            double consolidated = 0.0;
            if (iat1Found && iat2Found) {
                consolidated = (iat1Weighted + iat2Weighted) / 2.0;
            } else if (iat1Found) {
                consolidated = iat1Weighted;
            } else if (iat2Found) {
                consolidated = iat2Weighted;
            }
            subMap.put("consolidatedInternal", consolidated);
            subMap.put("finalInternal", Math.round(consolidated));

            subjectMarksList.add(subMap);
        }
        progressCard.put("internalPerformance", subjectMarksList);

        // 5. Cumulative Academic Semesters Overview
        List<SemesterResult> allSemestersResults = semesterResultRepository.findByStudent_Id(studentId);
        List<Map<String, Object>> progressList = new ArrayList<>();
        for (SemesterResult sr : allSemestersResults) {
            if ("PUBLISHED".equalsIgnoreCase(sr.getStatus()) || "LOCKED".equalsIgnoreCase(sr.getStatus())) {
                Map<String, Object> sMap = new HashMap<>();
                sMap.put("semesterName", sr.getSemesterName());
                sMap.put("sgpa", sr.getSgpa());
                progressList.add(sMap);
            }
        }

        progressList.sort((a, b) -> ((String) a.get("semesterName")).compareTo((String) b.get("semesterName")));
        progressCard.put("overallProgress", progressList);

        return progressCard;
    }
}
