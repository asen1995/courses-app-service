package com.school.repository;

import com.school.entity.Course;
import com.school.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Course} entities.
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Counts courses by their type.
     *
     * @param type the course type
     * @return the number of courses matching the type
     */
    Long countByType(CourseType type);
}
