package com.school.controller;

import com.school.dto.CountDto;
import com.school.dto.GroupCourseReportDto;
import com.school.dto.MemberDto;
import com.school.enums.CourseType;
import com.school.enums.MemberType;
import com.school.service.CourseService;
import com.school.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for reporting and analytics endpoints.
 * <p>
 * Provides read-only endpoints under {@code /reports} for counting members/courses
 * and filtering members by various criteria.
 */
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final MemberService memberService;
    private final CourseService courseService;

    /**
     * Constructs the controller with the required services.
     *
     * @param memberService the member service
     * @param courseService the course service
     */
    public ReportController(MemberService memberService, CourseService courseService) {
        this.memberService = memberService;
        this.courseService = courseService;
    }

    /**
     * Returns the count of members by type.
     *
     * @param type the member type (STUDENT or TEACHER)
     * @return the count
     */
    @GetMapping("/members/count")
    public ResponseEntity<CountDto> getMemberCount(@RequestParam MemberType type) {
        return ResponseEntity.ok(memberService.countMembersByType(type));
    }

    /**
     * Returns the count of courses by type.
     *
     * @param type the course type (MAIN or SECONDARY)
     * @return the count
     */
    @GetMapping("/courses/count")
    public ResponseEntity<CountDto> getCourseCountByType(@RequestParam CourseType type) {
        return ResponseEntity.ok(courseService.countCoursesByType(type));
    }

    /**
     * Retrieves members of a given type enrolled in a specific course.
     *
     * @param courseId the course ID
     * @param type     the member type
     * @return list of matching members
     */
    @GetMapping("/courses/members")
    public ResponseEntity<List<MemberDto>> getMembersByCourse(
            @RequestParam Long courseId,
            @RequestParam MemberType type) {
        return ResponseEntity.ok(memberService.findMembersByTypeAndCourseId(type, courseId));
    }

    /**
     * Retrieves all members belonging to a specific group.
     *
     * @param group the group name
     * @return list of members in the group
     */
    @GetMapping("/groups/members")
    public ResponseEntity<List<MemberDto>> getMembersByGroup(@RequestParam String group) {
        return ResponseEntity.ok(memberService.findMembersByGroup(group));
    }

    /**
     * Retrieves a report of members in a group enrolled in a specific course,
     * split by students and teachers.
     *
     * @param group    the group name
     * @param courseId the course ID
     * @return the group-course report
     */
    @GetMapping("/groups/courses")
    public ResponseEntity<GroupCourseReportDto> getMembersByGroupAndCourse(
            @RequestParam String group,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(memberService.findMembersByGroupAndCourseId(group, courseId));
    }

    /**
     * Filters members by type, minimum age, and course enrollment.
     *
     * @param minAge   the minimum age (inclusive)
     * @param courseId the course ID
     * @param type     the member type
     * @return list of matching members
     */
    @GetMapping("/members/filter")
    public ResponseEntity<List<MemberDto>> filterMembersByCriterias(
            @RequestParam Integer minAge,
            @RequestParam Long courseId,
            @RequestParam MemberType type) {
        return ResponseEntity.ok(
                memberService.findMembersByTypeAndAgeGreaterThanAndCourseId(
                        type, minAge, courseId));
    }
}
