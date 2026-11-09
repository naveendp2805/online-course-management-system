
package com.ocms.course.service;

import com.ocms.course.dto.CourseRequest;
import com.ocms.course.dto.CourseResponse;
import com.ocms.course.dto.CourseContentRequest;
import com.ocms.course.dto.CourseContentResponse;
import com.ocms.course.entity.Course;
import com.ocms.course.entity.CourseContent;
import com.ocms.course.enums.CourseStatus;
import com.ocms.course.repository.CourseRepository;

import com.ocms.exception.ResourceNotFoundException;
import com.ocms.user.entity.User;
import com.ocms.user.enums.Role;
import com.ocms.user.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(
            CourseRepository courseRepository,
            UserRepository userRepository) {

        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    // --------------------------------------------------
    // CREATE COURSE
    // --------------------------------------------------

    @Transactional
    public CourseResponse createCourse(
            CourseRequest request,
            String instructorEmail) {

        User instructor = getInstructor(instructorEmail);

        Course course = new Course();

        course.setTitle(request.title().trim());
        course.setDescription(request.description().trim());
        course.setCategory(request.category().trim());
        course.setInstructor(instructor);

        course.setStatus(CourseStatus.DRAFT);

        addContents(course, request.contents());

        Course savedCourse = courseRepository.save(course);

        return mapToResponse(savedCourse);
    }

    // --------------------------------------------------
    // UPDATE COURSE
    // --------------------------------------------------

    @Transactional
    public CourseResponse updateCourse(
            Long courseId,
            CourseRequest request,
            String instructorEmail) {

        Course course = getOwnedCourse(courseId, instructorEmail);

        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Archived courses cannot be updated"
            );
        }

        course.setTitle(request.title().trim());
        course.setDescription(request.description().trim());
        course.setCategory(request.category().trim());

        // Replace existing contents
        course.clearContents();

        addContents(course, request.contents());

        Course updatedCourse = courseRepository.save(course);

        return mapToResponse(updatedCourse);
    }

    // --------------------------------------------------
    // PUBLISH COURSE
    // --------------------------------------------------

    @Transactional
    public CourseResponse publishCourse(
            Long courseId,
            String instructorEmail) {

        Course course = getOwnedCourse(courseId, instructorEmail);

        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Archived courses cannot be published"
            );
        }

        if (course.getContents().isEmpty()) {
            throw new IllegalStateException(
                    "A course must contain learning materials before publishing"
            );
        }

        course.setStatus(CourseStatus.PUBLISHED);

        return mapToResponse(courseRepository.save(course));
    }

    // --------------------------------------------------
    // ARCHIVE COURSE
    // --------------------------------------------------

    @Transactional
    public CourseResponse archiveCourse(
            Long courseId,
            String instructorEmail) {

        Course course = getOwnedCourse(courseId, instructorEmail);

        course.setStatus(CourseStatus.ARCHIVED);

        return mapToResponse(courseRepository.save(course));
    }

    // --------------------------------------------------
    // GET PUBLIC COURSE DETAILS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public CourseResponse getPublishedCourse(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException(
                    "Course not found"
            );
        }

        return mapToResponse(course);
    }

    // --------------------------------------------------
    // GET INSTRUCTOR'S COURSE DETAILS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public CourseResponse getInstructorCourse(
            Long courseId,
            String instructorEmail) {

        Course course = getOwnedCourse(courseId, instructorEmail);

        return mapToResponse(course);
    }

    // --------------------------------------------------
    // LIST PUBLISHED COURSES
    // --------------------------------------------------

    public Page<CourseResponse> getPublishedCourses(
            String keyword,
            Pageable pageable) {

        Page<Course> courses;

        if (keyword == null || keyword.isBlank()) {
            courses = courseRepository.findByStatus(
                    CourseStatus.PUBLISHED,
                    pageable
            );
        } else {
            courses = courseRepository.searchPublishedCourses(
                    CourseStatus.PUBLISHED,
                    keyword.trim(),
                    pageable
            );
        }

        return courses.map(this::mapToResponse);
    }

    // --------------------------------------------------
    // LIST INSTRUCTOR'S COURSES
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public Page<CourseResponse> getInstructorCourses(
            String instructorEmail,
            Pageable pageable) {

        return courseRepository.findByInstructorEmail(
                        instructorEmail,
                        pageable
                )
                .map(this::mapToResponse);
    }

    // --------------------------------------------------
    // ADD CONTENTS
    // --------------------------------------------------

    private void addContents(
            Course course,
            List<CourseContentRequest> requests) {

        for (CourseContentRequest request : requests) {

            CourseContent content = new CourseContent();

            content.setTitle(request.title().trim());
            content.setDescription(request.description());
            content.setContentType(request.contentType());
            content.setResourceUrl(request.resourceUrl().trim());
            content.setSequenceOrder(request.sequenceOrder());

            course.addContent(content);
        }
    }

    // --------------------------------------------------
    // FIND INSTRUCTOR
    // --------------------------------------------------

    private User getInstructor(String email) {

        User instructor = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instructor not found"
                        )
                );

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new AccessDeniedException(
                    "Only instructors can manage courses"
            );
        }

        return instructor;
    }

    // --------------------------------------------------
    // CHECK COURSE OWNERSHIP
    // --------------------------------------------------

    private Course getOwnedCourse(
            Long courseId,
            String instructorEmail) {

        return courseRepository
                .findByIdAndInstructorEmail(
                        courseId,
                        instructorEmail
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );
    }

    // --------------------------------------------------
    // ENTITY TO DTO MAPPING
    // --------------------------------------------------

    private CourseResponse mapToResponse(Course course) {

        List<CourseContentResponse> contents =
                course.getContents()
                        .stream()
                        .map(this::mapContentToResponse)
                        .toList();

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getInstructor().getId(),
                course.getInstructor().getName(),
                course.getStatus(),
                contents,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }

    private CourseContentResponse mapContentToResponse(
            CourseContent content) {

        return new CourseContentResponse(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getContentType(),
                content.getResourceUrl(),
                content.getSequenceOrder()
        );
    }
}
