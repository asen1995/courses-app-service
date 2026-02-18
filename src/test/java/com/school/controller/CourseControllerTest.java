package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.CourseDto;
import com.school.enums.CourseType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static com.school.fixture.CourseFixture.courseDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseControllerTest {

    private static final String COURSES_PATH = "/courses";
    private static final String COURSE_BY_ID_PATH = COURSES_PATH + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCourse() throws Exception {
        var dto = courseDto("Math", CourseType.MAIN);

        mockMvc.perform(post(COURSES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.type").value(dto.getType().name()));
    }

    @Test
    void shouldGetCourseById() throws Exception {
        var course = createCourse("Math", CourseType.MAIN);

        mockMvc.perform(get(COURSE_BY_ID_PATH, course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(course.getName()));
    }

    @Test
    void shouldReturnNotFoundForMissingCourse() throws Exception {
        mockMvc.perform(get(COURSE_BY_ID_PATH, 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllCourses() throws Exception {
        createCourse("Math", CourseType.MAIN);
        createCourse("Art", CourseType.SECONDARY);

        mockMvc.perform(get(COURSES_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldUpdateCourse() throws Exception {
        var course = createCourse("Math", CourseType.MAIN);
        var updated = courseDto("Advanced Math", CourseType.MAIN);

        mockMvc.perform(put(COURSE_BY_ID_PATH, course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated.getName()));
    }

    @Test
    void shouldDeleteCourse() throws Exception {
        var course = createCourse("Math", CourseType.MAIN);

        mockMvc.perform(delete(COURSE_BY_ID_PATH, course.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(COURSE_BY_ID_PATH, course.getId()))
                .andExpect(status().isNotFound());
    }

    private CourseDto createCourse(String courseName, CourseType courseType) throws Exception {
        var dto = courseDto(courseName, courseType);
        var result = mockMvc.perform(post(COURSES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), CourseDto.class);
    }
}
