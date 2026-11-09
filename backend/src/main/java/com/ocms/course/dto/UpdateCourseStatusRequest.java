
package com.ocms.course.dto;

import com.ocms.course.enums.CourseStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateCourseStatusRequest(

        @NotNull(message = "Course status is required")
        CourseStatus status

) {
}
