package com.school.controller;

import com.school.dto.CourseDto;
import com.school.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing courses.
 * <p>
 * Provides CRUD endpoints under {@code /courses}.
 */
@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    /**
     * Constructs the controller with the required service.
     *
     * @param courseService the course service
     */
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Creates a new course.
     *
     * @param dto the course data
     * @return the created course with HTTP 201
     */
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(dto));
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param id the course ID
     * @return the course data
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    /**
     * Retrieves all courses.
     *
     * @return list of all courses
     */
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * Updates an existing course.
     *
     * @param id  the course ID
     * @param dto the updated course data
     * @return the updated course
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDto dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id the course ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
