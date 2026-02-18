package com.school.controller;

import com.school.dto.CountDto;
import com.school.dto.GroupCourseReportDto;
import com.school.dto.MemberDto;
import com.school.enums.CourseType;
import com.school.enums.MemberType;
import com.school.service.CourseService;
import com.school.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final MemberService memberService;
    private final CourseService courseService;

    public ReportController(MemberService memberService, CourseService courseService) {
        this.memberService = memberService;
        this.courseService = courseService;
    }

    @GetMapping("/members/count")
    public ResponseEntity<CountDto> getMemberCount(@RequestParam MemberType type) {
        return ResponseEntity.ok(memberService.countMembersByType(type));
    }

    @GetMapping("/courses/count")
    public ResponseEntity<CountDto> getCourseCountByType(@RequestParam CourseType type) {
        return ResponseEntity.ok(courseService.countCoursesByType(type));
    }

    @GetMapping("/courses/members")
    public ResponseEntity<List<MemberDto>> getMembersByCourse(@RequestParam Long courseId, @RequestParam MemberType type) {
        return ResponseEntity.ok(memberService.findMembersByTypeAndCourseId(type, courseId));
    }

    @GetMapping("/groups/members")
    public ResponseEntity<List<MemberDto>> getMembersByGroup(@RequestParam String group) {
        return ResponseEntity.ok(memberService.findMembersByGroup(group));
    }

    @GetMapping("/groups/courses")
    public ResponseEntity<GroupCourseReportDto> getMembersByGroupAndCourse(@RequestParam String group, @RequestParam Long courseId) {
        return ResponseEntity.ok(memberService.findMembersByGroupAndCourseId(group, courseId));
    }

    @GetMapping("/members/filter")
    public ResponseEntity<List<MemberDto>> filterMembersByCriterias(@RequestParam Integer minAge, @RequestParam Long courseId, @RequestParam MemberType type) {
        return ResponseEntity.ok(memberService.findMembersByTypeAndAgeGreaterThanAndCourseId(type, minAge, courseId));
    }
}
