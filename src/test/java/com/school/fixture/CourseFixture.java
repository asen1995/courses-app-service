package com.school.fixture;

import com.school.dto.CourseDto;
import com.school.entity.Course;
import com.school.enums.CourseType;

public class CourseFixture {

    public static CourseDto courseDto(String courseName, CourseType courseType) {
        return CourseDto.builder()
                .name(courseName)
                .type(courseType)
                .build();
    }

    public static Course courseEntity(Long id) {
        var course = new Course();
        course.setId(id);
        return course;
    }
}
