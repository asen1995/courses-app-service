package com.school.service;

import com.school.dto.CountDto;
import com.school.dto.CourseDto;
import com.school.entity.Course;
import com.school.enums.CourseType;
import com.school.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import com.school.mapper.CourseMapper;
import com.school.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for course management.
 * <p>
 * Handles business logic for creating, retrieving, updating, deleting,
 * and counting courses.
 */
@Slf4j
@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    /**
     * Constructs the service with the required dependencies.
     *
     * @param courseRepository the course repository
     * @param courseMapper     the course mapper
     */
    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    /**
     * Creates a new course.
     *
     * @param courseDto the course data
     * @return the created course
     */
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = courseMapper.toCourseEntity(courseDto);
        Course savedCourse = courseRepository.save(course);
        log.info("Created course with id: {}", savedCourse.getId());
        return courseMapper.toCourseDto(savedCourse);
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param id the course ID
     * @return the course data
     * @throws ResourceNotFoundException if the course is not found
     */
    @Transactional(readOnly = true)
    public CourseDto getCourseById(Long id) {
        return courseMapper.toCourseDto(findCourseById(id));
    }

    /**
     * Retrieves all courses.
     *
     * @return list of all courses
     */
    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toCourseDto)
                .toList();
    }

    /**
     * Updates an existing course.
     *
     * @param id  the course ID
     * @param courseDto the updated course data
     * @return the updated course
     * @throws ResourceNotFoundException if the course is not found
     */
    public CourseDto updateCourse(Long id, CourseDto courseDto) {
        Course course = findCourseById(id);
        courseMapper.updateCourseEntity(courseDto, course);
        Course savedCourse = courseRepository.save(course);
        log.info("Updated course with id: {}", savedCourse.getId());
        return courseMapper.toCourseDto(savedCourse);
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id the course ID
     * @throws ResourceNotFoundException if the course is not found
     */
    public void deleteCourse(Long id) {
        if (Boolean.FALSE.equals(courseRepository.existsById(id))) {
            throw new ResourceNotFoundException(
                    String.format("Course not found with id: %d", id));
        }
        courseRepository.deleteById(id);
    }

    /**
     * Counts courses by type.
     *
     * @param type the course type
     * @return the count wrapped in a DTO
     */
    @Transactional(readOnly = true)
    public CountDto countCoursesByType(CourseType type) {
        return new CountDto(courseRepository.countByType(type));
    }

    /** Finds a course by ID or throws {@link ResourceNotFoundException}. */
    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Course not found with id: %d", id)));
    }
}
