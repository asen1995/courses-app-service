package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Data transfer object for the group-course report.
 * <p>
 * Contains all members (students and teachers) belonging to a specific group
 * and enrolled in a specific course.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCourseReportDto {

    private String group;
    private Long courseId;
    private List<MemberDto> members;
}
