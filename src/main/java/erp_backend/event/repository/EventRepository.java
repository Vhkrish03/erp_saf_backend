package erp_backend.event.repository;

import erp_backend.event.entity.Event;
import erp_backend.event.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedById(Long userId);

    List<Event> findByStatus(EventStatus status);

    List<Event> findByOrganizerDepartment(String department);

    List<Event> findByStatusAndOrganizerDepartment(EventStatus status, String department);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' " +
            "AND (e.department IS NULL OR e.department = '' OR e.department = 'ALL' OR e.department = :department) " +
            "AND (e.year IS NULL OR e.year = '' OR e.year = 'ALL' OR e.year = :year) " +
            "AND (e.section IS NULL OR e.section = '' OR e.section = 'ALL' OR e.section = :section) " +
            "AND (e.targetAudience IS NULL OR e.targetAudience = '' OR e.targetAudience = 'ALL' OR e.targetAudience = 'STUDENTS' OR e.targetAudience = 'ALL_STUDENTS') "
            +
            "ORDER BY e.eventDate ASC, e.startTime ASC")
    List<Event> findPublishedEventsForStudent(@Param("department") String department,
            @Param("year") String year,
            @Param("section") String section);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' " +
            "AND (e.department IS NULL OR e.department = '' OR e.department = 'ALL' OR e.department = :department) " +
            "AND (e.targetAudience IS NULL OR e.targetAudience = '' OR e.targetAudience = 'ALL' OR e.targetAudience = 'TEACHERS' OR e.targetAudience = 'ALL_TEACHERS') "
            +
            "ORDER BY e.eventDate ASC, e.startTime ASC")
    List<Event> findPublishedEventsForTeacher(@Param("department") String department);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' " +
            "ORDER BY e.eventDate ASC, e.startTime ASC")
    List<Event> findAllPublishedEvents();
}
