
package com.ocms.course.dto;

import com.ocms.course.enums.ContentType;

public record CourseContentResponse(

        Long id,
        String title,
        String description,
        ContentType contentType,
        String resourceUrl,
        Integer sequenceOrder

) {
}
