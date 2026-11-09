
package com.ocms.course.controller;

import com.ocms.course.dto.CourseRequest;
import com.ocms.course.dto.CourseResponse;
import com.ocms.course.service.CourseService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // --------------------------------------------------
    // CREATE COURSE - INSTRUCTOR
    // --------------------------------------------------

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseRequest request,
            @AuthenticationPrincipal String instructorEmail) {

        CourseResponse response =
                courseService.createCourse(
                        request,
                        instructorEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // --------------------------------------------------
    // UPDATE COURSE - INSTRUCTOR
    // --------------------------------------------------

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseRequest request,
            @AuthenticationPrincipal String instructorEmail) {

        return ResponseEntity.ok(
                courseService.updateCourse(
                        courseId,
                        request,
                        instructorEmail
                )
        );
    }

    // --------------------------------------------------
    // PUBLISH COURSE - INSTRUCTOR
    // --------------------------------------------------

    @PatchMapping("/{courseId}/publish")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> publishCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal String instructorEmail) {

        return ResponseEntity.ok(
                courseService.publishCourse(
                        courseId,
                        instructorEmail
                )
        );
    }

    // --------------------------------------------------
    // ARCHIVE COURSE - INSTRUCTOR
    // --------------------------------------------------

    @PatchMapping("/{courseId}/archive")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> archiveCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal String instructorEmail) {

        return ResponseEntity.ok(
                courseService.archiveCourse(
                        courseId,
                        instructorEmail
                )
        );
    }

    // --------------------------------------------------
    // GET ALL PUBLISHED COURSES - PUBLIC
    // --------------------------------------------------

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getPublishedCourses(

            @RequestParam(required = false) String keyword,

            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC
            )
            Pageable pageable) {

        return ResponseEntity.ok(
                courseService.getPublishedCourses(
                        keyword,
                        pageable
                )
        );
    }

    // --------------------------------------------------
    // GET PUBLISHED COURSE DETAILS - PUBLIC
    // --------------------------------------------------

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                courseService.getPublishedCourse(courseId)
        );
    }

    // --------------------------------------------------
    // GET INSTRUCTOR'S COURSES
    // --------------------------------------------------

    @GetMapping("/instructor/my-courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Page<CourseResponse>> getMyCourses(
            @AuthenticationPrincipal String instructorEmail,

            @PageableDefault(size = 10)
            Pageable pageable) {

        return ResponseEntity.ok(
                courseService.getInstructorCourses(
                        instructorEmail,
                        pageable
                )
        );
    }

    // --------------------------------------------------
    // GET INSTRUCTOR'S COURSE DETAILS
    // --------------------------------------------------

    @GetMapping("/instructor/my-courses/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> getMyCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal String instructorEmail) {

        return ResponseEntity.ok(
                courseService.getInstructorCourse(
                        courseId,
                        instructorEmail
                )
        );
    }
}
