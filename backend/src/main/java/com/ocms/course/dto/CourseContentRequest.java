
package com.ocms.course.dto;

import com.ocms.course.enums.ContentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseContentRequest(

        @NotBlank(message = "Content title is required")
        @Size(max = 150)
        String title,

        @Size(max = 2000)
        String description,

        @NotNull(message = "Content type is required")
        ContentType contentType,

        @NotBlank(message = "Resource URL is required")
        @Size(max = 1000)
        String resourceUrl,

        @NotNull(message = "Sequence order is required")
        Integer sequenceOrder

) {
}
