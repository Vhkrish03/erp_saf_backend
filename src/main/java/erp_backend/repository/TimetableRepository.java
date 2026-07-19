package erp_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findByDay(String day);

}