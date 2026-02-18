package com.school.fixture;

import com.school.dto.CourseDto;
import com.school.enums.CourseType;

public class CourseFixture {

    public static CourseDto courseDto(String courseName, CourseType courseType) {
        return CourseDto.builder()
                .name(courseName)
                .type(courseType)
                .build();
    }
}
