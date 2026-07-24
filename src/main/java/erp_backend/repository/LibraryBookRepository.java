package erp_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.LibraryBook;

public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {

    List<LibraryBook> findByStudentId(String studentId);

}
