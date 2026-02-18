package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.CourseDto;
import com.school.dto.MemberDto;
import com.school.enums.CourseType;
import com.school.enums.MemberType;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseDto math;
    private CourseDto art;
    private CourseDto physics;

    private static final String COURSES_PATH = "/courses";
    private static final String MEMBERS_PATH = "/members";
    private static final String MEMBERS_COUNT_PATH = "/reports/members/count";
    private static final String COURSES_COUNT_PATH = "/reports/courses/count";
    private static final String COURSE_MEMBERS_PATH = "/reports/courses/members";
    private static final String GROUP_MEMBERS_PATH = "/reports/groups/members";
    private static final String GROUP_COURSE_PATH = "/reports/groups/courses";
    private static final String MEMBERS_FILTER_PATH = "/reports/members/filter";

    private static final String GROUP_A1 = "A1";
    private static final String GROUP_B1 = "B1";

    @BeforeEach
    void setUp() throws Exception {
        math = createCourse("Math", CourseType.MAIN);
        art = createCourse("Art", CourseType.SECONDARY);
        physics = createCourse("Physics", CourseType.MAIN);

        // Create students
        createMember("John", 20, GROUP_A1, MemberType.STUDENT, Set.of(math.getId(), art.getId()));
        createMember("Jane", 22, GROUP_A1, MemberType.STUDENT, Set.of(math.getId()));
        createMember("Bob", 18, GROUP_B1, MemberType.STUDENT, Set.of(art.getId(), physics.getId()));
        createMember("Alice", 25, GROUP_A1, MemberType.STUDENT, Set.of(math.getId(), physics.getId()));

        // Create teachers
        createMember("Prof Smith", 45, GROUP_A1, MemberType.TEACHER, Set.of(math.getId()));
        createMember("Prof Jones", 50, GROUP_B1, MemberType.TEACHER, Set.of(art.getId(), physics.getId()));
    }

    @Test
    void shouldReturnStudentCount() throws Exception {
        mockMvc.perform(get(MEMBERS_COUNT_PATH).param("type", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(4));
    }

    @Test
    void shouldReturnTeacherCount() throws Exception {
        mockMvc.perform(get(MEMBERS_COUNT_PATH).param("type", "TEACHER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void shouldReturnCourseCountByType() throws Exception {
        mockMvc.perform(get(COURSES_COUNT_PATH).param("type", "MAIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2));

        mockMvc.perform(get(COURSES_COUNT_PATH).param("type", "SECONDARY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void shouldReturnStudentsByCourse() throws Exception {
        // Math course has John, Jane, Alice as students
        mockMvc.perform(get(COURSE_MEMBERS_PATH)
                        .param("courseId", math.getId().toString())
                        .param("type", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void shouldReturnMembersByGroup() throws Exception {
        // Group A1 has John, Jane, Alice (students) + Prof Smith (teacher) = 4
        mockMvc.perform(get(GROUP_MEMBERS_PATH).param("group", GROUP_A1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void shouldReturnMembersByGroupAndCourse() throws Exception {
        // Group A1, Math course: students = John, Jane, Alice; teacher = Prof Smith = 4 members
        mockMvc.perform(get(GROUP_COURSE_PATH)
                        .param("group", GROUP_A1)
                        .param("courseId", math.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.group").value(GROUP_A1))
                .andExpect(jsonPath("$.courseId").value(math.getId()))
                .andExpect(jsonPath("$.members.length()").value(4));
    }

    @Test
    void shouldReturnStudentsOlderThanAgeInCourse() throws Exception {
        // Students older than 20 in Math: Jane (22), Alice (25)
        mockMvc.perform(get(MEMBERS_FILTER_PATH)
                        .param("minAge", "20")
                        .param("courseId", math.getId().toString())
                        .param("type", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnEmptyWhenNoMembersMatchFilter() throws Exception {
        // Students older than 30 in Math: none
        mockMvc.perform(get(MEMBERS_FILTER_PATH)
                        .param("minAge", "30")
                        .param("courseId", math.getId().toString())
                        .param("type", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
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
