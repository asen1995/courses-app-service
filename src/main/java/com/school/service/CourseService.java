package com.school.service;

import com.school.dto.CountDto;
import com.school.dto.CourseDto;
import com.school.enums.CourseType;
import com.school.exception.ResourceNotFoundException;
import com.school.mapper.CourseMapper;
import com.school.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public CourseDto createCourse(CourseDto dto) {
        var course = courseMapper.toEntity(dto);
        return courseMapper.toDto(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public CourseDto getCourseById(Long id) {
        return courseMapper.toDto(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(courseMapper::toDto).toList();
    }

    public CourseDto updateCourse(Long id, CourseDto dto) {
        var course = findOrThrow(id);
        courseMapper.updateEntity(dto, course);
        return courseMapper.toDto(courseRepository.save(course));
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CountDto countCoursesByType(CourseType type) {
        return new CountDto(courseRepository.countByType(type));
    }

    private com.school.entity.Course findOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }
}
