package com.school.dto;

import com.school.enums.CourseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for course data.
 * <p>
 * Used for both request and response payloads in course endpoints.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {

    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private CourseType type;
}
