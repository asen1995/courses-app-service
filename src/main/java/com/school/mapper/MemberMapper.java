package com.school.mapper;

import com.school.dto.MemberDto;
import com.school.entity.Course;
import com.school.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "courseIds", source = "courses", qualifiedByName = "coursesToIds")
    MemberDto toDto(Member member);

    @Mapping(target = "courses", ignore = true)
    Member toEntity(MemberDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntity(MemberDto dto, @MappingTarget Member member);

    @Named("coursesToIds")
    default Set<Long> coursesToIds(Set<Course> courses) {
        if (courses == null) {
            return Collections.emptySet();
        }
        return courses.stream().map(Course::getId).collect(Collectors.toSet());
    }
}
