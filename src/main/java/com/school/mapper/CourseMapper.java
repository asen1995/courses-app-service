package com.school.mapper;

import com.school.dto.CourseDto;
import com.school.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDto toDto(Course course);

    @Mapping(target = "members", ignore = true)
    Course toEntity(CourseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    void updateEntity(CourseDto dto, @MappingTarget Course course);
}
