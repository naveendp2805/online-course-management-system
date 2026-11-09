package com.ocms.course.repository;

import com.ocms.course.entity.Course;
import com.ocms.course.enums.CourseStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Instructor's own courses
    Page<Course> findByInstructorEmail(
            String email,
            Pageable pageable
    );

    // Find a course owned by a particular instructor
    Optional<Course> findByIdAndInstructorEmail(
            Long id,
            String email
    );

    @Query(
            value = """
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.status = :status
        """,
            countQuery = """
        SELECT COUNT(c) FROM Course c
        WHERE c.status = :status
        """
    )
    Page<Course> findByStatus(
            @Param("status") CourseStatus status,
            Pageable pageable
    );

    @Query(
            value = """
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.status = :status
        AND (
            LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """,
            countQuery = """
        SELECT COUNT(c) FROM Course c
        WHERE c.status = :status
        AND (
            LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """
    )
    Page<Course> searchPublishedCourses(
            @Param("status") CourseStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // Check whether instructor owns the course
    boolean existsByIdAndInstructorEmail(
            Long id,
            String email
    );
}
