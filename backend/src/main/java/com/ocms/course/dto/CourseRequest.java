
package com.ocms.course.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CourseRequest(

        @NotBlank(message = "Course title is required")
        @Size(max = 150)
        String title,

        @NotBlank(message = "Course description is required")
        String description,

        @NotBlank(message = "Course category is required")
        @Size(max = 100)
        String category,

        @NotEmpty(message = "At least one course content item is required")
        List<@Valid CourseContentRequest> contents

) {
}
