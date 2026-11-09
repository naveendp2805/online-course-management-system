
package com.ocms.course.dto;

import com.ocms.course.enums.CourseStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CourseResponse(

        Long id,
        String title,
        String description,
        String category,

        Long instructorId,
        String instructorName,

        CourseStatus status,

        List<CourseContentResponse> contents,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
