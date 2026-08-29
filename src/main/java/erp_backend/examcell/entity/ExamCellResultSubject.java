package erp_backend.examcell.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Individual subject result within an official Exam Cell result record.
 *
 * Stores per-subject: subject code/name, credits, grade, grade point, result
 * status.
 * SGPA is stored on the parent ExamCellResult after calculation.
 */
@Entity
@Table(name = "exam_cell_result_subjects")
public class ExamCellResultSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_cell_result_id", nullable = false)
    @JsonIgnore
    private ExamCellResult examCellResult;

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    private int credits;

    /** Grade awarded: O, A+, A, B+, B, C, U, RA, UA, W */
    @Column(length = 10)
    private String grade;

    @Column(name = "grade_point")
    private double gradePoint;

    /**
     * Result status: PASS, FAIL, WITHHELD, NOT_APPEARED, RESULT_AWAITED
     */
    @Column(name = "result_status", length = 30)
    private String resultStatus;

    /** Marks obtained in end-semester exam (university marks), if provided */
    @Column(name = "marks_obtained")
    private Double marksObtained;

    /** Max marks for end-semester exam (university side) */
    @Column(name = "max_marks")
    private Double maxMarks;

    /** Number of arrears / number of times appeared for this subject */
    @Column(name = "attempt_number")
    private Integer attemptNumber;

    public ExamCellResultSubject() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamCellResult getExamCellResult() {
        return examCellResult;
    }

    public void setExamCellResult(ExamCellResult examCellResult) {
        this.examCellResult = examCellResult;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }
}
