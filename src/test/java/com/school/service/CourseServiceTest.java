package com.school.service;

import com.school.dto.CourseDto;
import com.school.entity.Course;
import com.school.enums.CourseType;
import com.school.exception.ResourceNotFoundException;
import com.school.mapper.CourseMapper;
import com.school.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    @Test
    void shouldCreateCourse() {
        var dto = CourseDto.builder()
                .name("Math")
                .type(CourseType.MAIN)
                .build();
        var entity = new Course();
        var savedEntity = new Course();
        savedEntity.setId(1L);
        var expectedDto = CourseDto.builder()
                .id(1L)
                .name("Math")
                .type(CourseType.MAIN)
                .build();

        when(courseMapper.toEntity(dto)).thenReturn(entity);
        when(courseRepository.save(entity)).thenReturn(savedEntity);
        when(courseMapper.toDto(savedEntity)).thenReturn(expectedDto);

        var result = courseService.createCourse(dto);

        assertThat(result).isEqualTo(expectedDto);
        verify(courseMapper).toEntity(dto);
        verify(courseRepository).save(entity);
        verify(courseMapper).toDto(savedEntity);
    }

    @Test
    void shouldGetCourseById() {
        var entity = new Course();
        entity.setId(1L);
        var expectedDto = CourseDto.builder()
                .id(1L)
                .name("Math")
                .type(CourseType.MAIN)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(courseMapper.toDto(entity)).thenReturn(expectedDto);

        var result = courseService.getCourseById(1L);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void shouldThrowWhenCourseNotFoundById() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with id: 999");
    }

    @Test
    void shouldGetAllCourses() {
        var entity1 = new Course();
        var entity2 = new Course();
        var dto1 = CourseDto.builder().id(1L).name("Math").type(CourseType.MAIN).build();
        var dto2 = CourseDto.builder().id(2L).name("Art").type(CourseType.SECONDARY).build();

        when(courseRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(courseMapper.toDto(entity1)).thenReturn(dto1);
        when(courseMapper.toDto(entity2)).thenReturn(dto2);

        var result = courseService.getAllCourses();

        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    void shouldUpdateCourse() {
        var entity = new Course();
        entity.setId(1L);
        var dto = CourseDto.builder()
                .name("Advanced Math")
                .type(CourseType.MAIN)
                .build();
        var savedEntity = new Course();
        var expectedDto = CourseDto.builder()
                .id(1L)
                .name("Advanced Math")
                .type(CourseType.MAIN)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(courseRepository.save(entity)).thenReturn(savedEntity);
        when(courseMapper.toDto(savedEntity)).thenReturn(expectedDto);

        var result = courseService.updateCourse(1L, dto);

        assertThat(result).isEqualTo(expectedDto);
        verify(courseMapper).updateEntity(dto, entity);
        verify(courseRepository).save(entity);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentCourse() {
        var dto = CourseDto.builder().name("Math").type(CourseType.MAIN).build();

        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.updateCourse(999L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with id: 999");
    }

    @Test
    void shouldDeleteCourse() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        courseService.deleteCourse(1L);

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentCourse() {
        when(courseRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> courseService.deleteCourse(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with id: 999");
    }

    @Test
    void shouldCountCoursesByType() {
        when(courseRepository.countByType(CourseType.MAIN)).thenReturn(3L);

        var result = courseService.countCoursesByType(CourseType.MAIN);

        assertThat(result.getCount()).isEqualTo(3L);
    }
}
