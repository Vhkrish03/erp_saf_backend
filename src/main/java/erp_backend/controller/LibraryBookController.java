package erp_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.entity.LibraryBook;
import erp_backend.service.LibraryBookService;

@RestController
@RequestMapping("/api/library")
@CrossOrigin("*")
public class LibraryBookController {

    private final LibraryBookService service;

    public LibraryBookController(LibraryBookService service) {
        this.service = service;
    }

    @GetMapping("/{studentId}")
    public List<LibraryBook> getBooks(@PathVariable String studentId) {
        return service.getBooks(studentId);
    }
}