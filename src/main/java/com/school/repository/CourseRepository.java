package com.school.repository;

import com.school.entity.Course;
import com.school.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Long countByType(CourseType type);
}
