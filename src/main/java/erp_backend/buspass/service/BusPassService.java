package erp_backend.buspass.service;

import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.buspass.entity.BusPass;
import erp_backend.buspass.repository.BusPassRepository;
import erp_backend.buspass.dto.BusPassResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusPassService {

    @Autowired
    private BusPassRepository busPassRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public BusPassResponse getStudentBusPass(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        BusPass pass = busPassRepository.findByPersonIdAndPersonType(studentId, "STUDENT")
                .orElseGet(() -> {
                    BusPass newPass = new BusPass();
                    newPass.setPersonId(studentId);
                    newPass.setPersonType("STUDENT");
                    newPass.setPassNumber(
                            "BP-ST-" + (student.getRollNumber() != null ? student.getRollNumber() : student.getId()));
                    newPass.setRoute("MANNIVAKKAM");
                    newPass.setPickupPoint("MANNIVAKKAM");
                    newPass.setDropPoint("KVAL COLLEGE");
                    newPass.setPickupTime("07:30 AM");
                    newPass.setValidFrom("2026-06-01");
                    newPass.setValidUntil("2027-05-31");
                    newPass.setAcademicYear("2026-27");
                    newPass.setStatus("VALID");
                    newPass.setAddress(
                            student.getAddress() != null && !student.getAddress().isEmpty() ? student.getAddress()
                                    : "NO 27, SRI LAKSHMI NAGAR ARAMBAKKAM VILLAGE KANCHEEPURAM");
                    return busPassRepository.save(newPass);
                });

        BusPassResponse resp = new BusPassResponse();
        resp.setName(student.getName());
        resp.setPersonId(studentId);
        resp.setPersonType("STUDENT");
        resp.setRollNumber(student.getRollNumber() != null ? student.getRollNumber() : student.getId());
        resp.setDepartment(student.getDepartment());
        resp.setYear(student.getYear());
        resp.setSection(student.getSection());
        resp.setDesignation("STUDENT");
        resp.setPassNumber(pass.getPassNumber());
        resp.setRoute(pass.getRoute());
        resp.setPickupPoint(pass.getPickupPoint());
        resp.setDropPoint(pass.getDropPoint());
        resp.setPickupTime(pass.getPickupTime());
        resp.setValidFrom(pass.getValidFrom());
        resp.setValidUntil(pass.getValidUntil());
        resp.setAddress(pass.getAddress());
        resp.setAcademicYear(pass.getAcademicYear());
        resp.setStatus(pass.getStatus());
        resp.setPhotoUrl("");
        return resp;
    }

    public BusPassResponse getTeacherBusPass(String employeeId) {
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with Employee ID: " + employeeId));

        BusPass pass = busPassRepository.findByPersonIdAndPersonType(employeeId, "TEACHER")
                .orElseGet(() -> {
                    BusPass newPass = new BusPass();
                    newPass.setPersonId(employeeId);
                    newPass.setPersonType("TEACHER");
                    newPass.setPassNumber("BP-TF-" + teacher.getEmployeeId());
                    newPass.setRoute("TAMBARAM ROUTE");
                    newPass.setPickupPoint("TAMBARAM BUS STOP");
                    newPass.setDropPoint("KVAL COLLEGE");
                    newPass.setPickupTime("07:30 AM");
                    newPass.setValidFrom("2026-06-01");
                    newPass.setValidUntil("2027-05-31");
                    newPass.setAcademicYear("2026-27");
                    newPass.setStatus("VALID");
                    newPass.setAddress(
                            teacher.getAddress() != null && !teacher.getAddress().isEmpty() ? teacher.getAddress()
                                    : "Chinna Kolambakkam, Tamil Nadu");
                    return busPassRepository.save(newPass);
                });

        BusPassResponse resp = new BusPassResponse();
        resp.setName(teacher.getName());
        resp.setPersonId(employeeId);
        resp.setPersonType("TEACHER");
        resp.setRollNumber(teacher.getEmployeeId());
        resp.setDepartment(teacher.getDepartment());
        resp.setDesignation(teacher.getDesignation() != null ? teacher.getDesignation() : "Assistant Professor");
        resp.setPassNumber(pass.getPassNumber());
        resp.setRoute(pass.getRoute());
        resp.setPickupPoint(pass.getPickupPoint());
        resp.setDropPoint(pass.getDropPoint());
        resp.setPickupTime(pass.getPickupTime());
        resp.setValidFrom(pass.getValidFrom());
        resp.setValidUntil(pass.getValidUntil());
        resp.setAddress(pass.getAddress());
        resp.setAcademicYear(pass.getAcademicYear());
        resp.setStatus(pass.getStatus());
        resp.setPhotoUrl(teacher.getPhotoUrl() != null ? teacher.getPhotoUrl() : "");
        return resp;
    }
}
