package erp_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.entity.Notice;
import erp_backend.service.NoticeService;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin("*")
public class NoticeController {

    private final NoticeService service;

    public NoticeController(NoticeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Notice> getAllNotices(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String department) {
        return service.getNotices(role, department);
    }

    @PostMapping
    public ResponseEntity<?> createNotice(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "purpose", required = false) String purpose,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "department", defaultValue = "ALL") String department,
            @RequestParam(value = "uploaderRole", defaultValue = "ADMIN") String uploaderRole,
            @RequestParam(value = "isImportant", defaultValue = "false") boolean isImportant,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setDescription(description);
        notice.setPurpose(purpose);
        notice.setCategory(category);
        notice.setDepartment(department);
        notice.setUploaderRole(uploaderRole);
        notice.setImportant(isImportant);
        notice.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        if (file != null && !file.isEmpty()) {
            try {
                String uploadsDir = "uploads/";
                File directory = new File(uploadsDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String originalName = file.getOriginalFilename();
                String ext = originalName != null && originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : "";
                String fileName = UUID.randomUUID().toString() + ext;
                Path filePath = Paths.get(uploadsDir + fileName);
                Files.copy(file.getInputStream(), filePath);
                // Assume the app serves /uploads/** 
                notice.setFileUrl("/uploads/" + fileName);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body("Failed to upload file");
            }
        }

        Notice saved = service.createNotice(notice);
        return ResponseEntity.ok(saved);
    }
}
