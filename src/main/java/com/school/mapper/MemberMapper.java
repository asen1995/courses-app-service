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

/**
 * MapStruct mapper for converting between {@link Member} entities and {@link MemberDto} objects.
 * <p>
 * Course associations are mapped between entity sets and ID sets via {@link #coursesToIds(Set)}.
 * Course entity resolution is handled in the service layer.
 */
@Mapper(componentModel = "spring")
public interface MemberMapper {

    /**
     * Converts a member entity to a DTO.
     *
     * @param member the member entity
     * @return the member DTO
     */
    @Mapping(target = "courseIds", source = "courses", qualifiedByName = "coursesToIds")
    MemberDto toMemberDto(Member member);

    /**
     * Converts a member DTO to an entity. Course associations are ignored
     * and must be set separately.
     *
     * @param dto the member DTO
     * @return the member entity
     */
    @Mapping(target = "courses", ignore = true)
    Member toMemberEntity(MemberDto dto);

    /**
     * Updates an existing member entity from a DTO. ID and course associations
     * are ignored and must be handled separately.
     *
     * @param dto    the source DTO
     * @param member the target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateMemberEntity(MemberDto dto, @MappingTarget Member member);

    /**
     * Extracts course IDs from a set of course entities.
     *
     * @param courses the course entities
     * @return set of course IDs, or empty set if null
     */
    @Named("coursesToIds")
    default Set<Long> coursesToIds(Set<Course> courses) {
        if (courses == null) {
            return Collections.emptySet();
        }
        return courses.stream().map(Course::getId).collect(Collectors.toSet());
    }
}
