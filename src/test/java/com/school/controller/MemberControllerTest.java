package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.CourseDto;
import com.school.dto.MemberDto;
import com.school.enums.CourseType;
import com.school.enums.MemberType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static com.school.fixture.CourseFixture.courseDto;
import static com.school.fixture.MemberFixture.memberDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemberControllerTest {

    private static final String MEMBERS_PATH = "/members";
    private static final String MEMBER_BY_ID_PATH = MEMBERS_PATH + "/{id}";
    private static final String COURSES_PATH = "/courses";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateStudent() throws Exception {
        var course = createCourse("Math", CourseType.MAIN);
        var dto = memberDto("John", 20, "A1", MemberType.STUDENT, Set.of(course.getId()));

        mockMvc.perform(post(MEMBERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.age").value(dto.getAge()))
                .andExpect(jsonPath("$.group").value(dto.getGroup()))
                .andExpect(jsonPath("$.type").value("STUDENT"));
    }

    @Test
    void shouldCreateTeacher() throws Exception {
        var course = createCourse("Math", CourseType.MAIN);
        var dto = memberDto("Prof Smith", 45, "A1", MemberType.TEACHER, Set.of(course.getId()));

        mockMvc.perform(post(MEMBERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.type").value("TEACHER"));
    }

    @Test
    void shouldGetMemberById() throws Exception {
        var member = createMember("John", 20, "A1", MemberType.STUDENT, Set.of());

        mockMvc.perform(get(MEMBER_BY_ID_PATH, member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(member.getName()));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingMemberWithNonExistentCourse() throws Exception {
        var dto = memberDto("John", 20, "A1", MemberType.STUDENT, Set.of(999L));

        mockMvc.perform(post(MEMBERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForMissingMember() throws Exception {
        mockMvc.perform(get(MEMBER_BY_ID_PATH, 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetMembersByType() throws Exception {
        createMember("John", 20, "A1", MemberType.STUDENT, Set.of());
        createMember("Jane", 22, "A2", MemberType.STUDENT, Set.of());
        createMember("Prof Smith", 45, "A1", MemberType.TEACHER, Set.of());

        mockMvc.perform(get(MEMBERS_PATH).param("type", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get(MEMBERS_PATH).param("type", "TEACHER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldUpdateMember() throws Exception {
        var member = createMember("John", 20, "A1", MemberType.STUDENT, Set.of());
        var updated = memberDto("John Updated", 21, "B1", MemberType.STUDENT, Set.of());

        mockMvc.perform(put(MEMBER_BY_ID_PATH, member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated.getName()))
                .andExpect(jsonPath("$.age").value(updated.getAge()))
                .andExpect(jsonPath("$.group").value(updated.getGroup()));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMemberWithNonExistentCourse() throws Exception {
        var member = createMember("John", 20, "A1", MemberType.STUDENT, Set.of());
        var updated = memberDto("John Updated", 21, "B1", MemberType.STUDENT, Set.of(999L));

        mockMvc.perform(put(MEMBER_BY_ID_PATH, member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteMember() throws Exception {
        var member = createMember("John", 20, "A1", MemberType.STUDENT, Set.of());

        mockMvc.perform(delete(MEMBER_BY_ID_PATH, member.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(MEMBER_BY_ID_PATH, member.getId()))
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

    private MemberDto createMember(String memberName, int memberAge, String memberGroup, MemberType memberType, Set<Long> courseIds) throws Exception {
        var dto = memberDto(memberName, memberAge, memberGroup, memberType, courseIds);
        var result = mockMvc.perform(post(MEMBERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), MemberDto.class);
    }
}
