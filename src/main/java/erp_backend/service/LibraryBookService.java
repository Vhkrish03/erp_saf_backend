package erp_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.entity.LibraryBook;
import erp_backend.repository.LibraryBookRepository;

@Service
public class LibraryBookService {

    private final LibraryBookRepository repository;

    public LibraryBookService(LibraryBookRepository repository) {
        this.repository = repository;
    }

    public List<LibraryBook> getBooks(String studentId) {
        return repository.findByStudentId(studentId);
    }
}