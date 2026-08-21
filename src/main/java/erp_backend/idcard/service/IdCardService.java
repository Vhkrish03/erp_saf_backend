package erp_backend.idcard.service;

import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.idcard.entity.IdCard;
import erp_backend.idcard.repository.IdCardRepository;
import erp_backend.idcard.dto.IdCardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdCardService {

    @Autowired
    private IdCardRepository idCardRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public IdCardResponse getStudentIdCard(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        IdCard card = idCardRepository.findByPersonIdAndPersonType(studentId, "STUDENT")
                .orElseGet(() -> {
                    IdCard newCard = new IdCard();
                    newCard.setPersonId(studentId);
                    newCard.setPersonType("STUDENT");
                    newCard.setValidity("2023-27");
                    newCard.setDayscholarStatus("DAYSCHOLAR");
                    newCard.setBarcodeData(student.getRollNumber() != null ? student.getRollNumber() : student.getId());
                    newCard.setStatus("ACTIVE");
                    return idCardRepository.save(newCard);
                });

        IdCardResponse resp = new IdCardResponse();
        resp.setName(student.getName());
        resp.setIdOrRollNumber(student.getRollNumber() != null ? student.getRollNumber() : student.getId());
        resp.setDepartment(student.getDepartment());
        resp.setSection(student.getSection());
        resp.setYear(student.getYear());
        resp.setSemester(student.getSemester());
        resp.setDesignation("STUDENT");
        resp.setValidity(card.getValidity());
        resp.setDayscholarStatus(card.getDayscholarStatus());
        resp.setBarcodeData(card.getBarcodeData());
        resp.setBloodGroup(
                student.getBloodGroup() != null && !student.getBloodGroup().isEmpty() ? student.getBloodGroup()
                        : "AB+VE");
        resp.setDob(student.getDob() != null && !student.getDob().isEmpty() ? student.getDob() : "03-08-2005");
        resp.setEmergencyContact(
                student.getEmergencyContactPhone() != null && !student.getEmergencyContactPhone().isEmpty()
                        ? student.getEmergencyContactPhone()
                        : "9500002487");
        resp.setAddress(student.getAddress() != null && !student.getAddress().isEmpty() ? student.getAddress()
                : "NO 27, SRI LAKSHMI NAGAR ARAMBAKKAM VILLAGE KANCHEEPURAM 601301");
        resp.setPhotoUrl("");
        resp.setStatus(card.getStatus());
        return resp;
    }

    public IdCardResponse getTeacherIdCard(String employeeId) {
        Teacher teacher = teacherRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with Employee ID: " + employeeId));

        IdCard card = idCardRepository.findByPersonIdAndPersonType(employeeId, "TEACHER")
                .orElseGet(() -> {
                    IdCard newCard = new IdCard();
                    newCard.setPersonId(employeeId);
                    newCard.setPersonType("TEACHER");
                    newCard.setValidity("LIFETIME");
                    newCard.setDayscholarStatus("STAFF");
                    newCard.setBarcodeData(employeeId);
                    newCard.setStatus("ACTIVE");
                    return idCardRepository.save(newCard);
                });

        IdCardResponse resp = new IdCardResponse();
        resp.setName(teacher.getName());
        resp.setIdOrRollNumber(teacher.getEmployeeId());
        resp.setDepartment(teacher.getDepartment());
        resp.setDesignation(teacher.getDesignation() != null ? teacher.getDesignation() : "Assistant Professor");
        resp.setValidity(card.getValidity());
        resp.setDayscholarStatus(card.getDayscholarStatus());
        resp.setBarcodeData(card.getBarcodeData());
        resp.setBloodGroup("A+VE");
        resp.setDob(teacher.getDob() != null ? teacher.getDob().toString() : "15-08-1985");
        resp.setEmergencyContact(
                teacher.getEmergencyContactNumber() != null && !teacher.getEmergencyContactNumber().isEmpty()
                        ? teacher.getEmergencyContactNumber()
                        : "9500000001");
        resp.setAddress(teacher.getAddress() != null && !teacher.getAddress().isEmpty() ? teacher.getAddress()
                : "1ST Road, Chinna Kolambakkam, Maduranthagam (Tk), TN");
        resp.setPhotoUrl(teacher.getPhotoUrl() != null ? teacher.getPhotoUrl() : "");
        resp.setStatus(card.getStatus());
        return resp;
    }
}
