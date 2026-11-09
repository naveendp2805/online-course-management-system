package com.ocms.course.repository;

import com.ocms.course.entity.CourseContent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseContentRepository
        extends JpaRepository<CourseContent, Long> {

    List<CourseContent> findByCourseIdOrderBySequenceOrderAsc(
            Long courseId
    );
}
