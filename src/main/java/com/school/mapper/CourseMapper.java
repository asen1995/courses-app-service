package com.school.mapper;

import com.school.dto.CourseDto;
import com.school.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between {@link Course} entities and {@link CourseDto} objects.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper {

    /**
     * Converts a course entity to a DTO.
     *
     * @param course the course entity
     * @return the course DTO
     */
    CourseDto toCourseDto(Course course);

    /**
     * Converts a course DTO to an entity. Member associations are ignored.
     *
     * @param dto the course DTO
     * @return the course entity
     */
    @Mapping(target = "members", ignore = true)
    Course toCourseEntity(CourseDto dto);

    /**
     * Updates an existing course entity from a DTO. ID and member associations are ignored.
     *
     * @param dto    the source DTO
     * @param course the target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    void updateCourseEntity(CourseDto dto, @MappingTarget Course course);
}
