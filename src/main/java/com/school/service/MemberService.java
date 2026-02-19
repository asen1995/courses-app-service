package com.school.service;

import com.school.dto.CountDto;
import com.school.dto.GroupCourseReportDto;
import com.school.dto.MemberDto;
import com.school.entity.Course;
import com.school.entity.Member;
import com.school.enums.MemberType;
import com.school.exception.ResourceNotFoundException;
import com.school.mapper.MemberMapper;
import com.school.repository.CourseRepository;
import com.school.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final MemberMapper memberMapper;

    public MemberService(MemberRepository memberRepository, CourseRepository courseRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository;
        this.memberMapper = memberMapper;
    }

    public MemberDto createMember(MemberDto dto) {
        var member = memberMapper.toEntity(dto);
        member.setCourses(resolveCourses(dto.getCourseIds()));
        return memberMapper.toDto(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public MemberDto getMemberById(Long id) {
        return memberMapper.toDto(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<MemberDto> getMembersByType(MemberType type) {
        return memberRepository.findByType(type).stream().map(memberMapper::toDto).toList();
    }

    public MemberDto updateMember(Long id, MemberDto dto) {
        var member = findOrThrow(id);
        memberMapper.updateEntity(dto, member);
        member.setCourses(resolveCourses(dto.getCourseIds()));
        return memberMapper.toDto(memberRepository.save(member));
    }

    public void deleteMember(Long id) {
        if (Boolean.FALSE.equals(memberRepository.existsById(id))) {
            throw new ResourceNotFoundException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CountDto countMembersByType(MemberType type) {
        return new CountDto(memberRepository.countByType(type));
    }

    @Transactional(readOnly = true)
    public List<MemberDto> findMembersByTypeAndCourseId(MemberType type, Long courseId) {
        validateCourseExists(courseId);
        return memberRepository.findByTypeAndCoursesId(type, courseId).stream().map(memberMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<MemberDto> findMembersByGroup(String group) {
        return memberRepository.findByGroup(group).stream().map(memberMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<MemberDto> findMembersByTypeAndGroupAndCourseId(MemberType type, String group, Long courseId) {
        return memberRepository.findByTypeAndGroupAndCoursesId(type, group, courseId).stream().map(memberMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public GroupCourseReportDto findMembersByGroupAndCourseId(String group, Long courseId) {
        validateCourseExists(courseId);
        List<MemberDto> students = findMembersByTypeAndGroupAndCourseId(MemberType.STUDENT, group, courseId);
        List<MemberDto> teachers = findMembersByTypeAndGroupAndCourseId(MemberType.TEACHER, group, courseId);
        List<MemberDto> allMembers = new ArrayList<>(students);
        allMembers.addAll(teachers);
        return GroupCourseReportDto.builder()
                .group(group)
                .courseId(courseId)
                .members(allMembers)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MemberDto> findMembersByTypeAndAgeGreaterThanAndCourseId(MemberType type, int age, Long courseId) {
        validateCourseExists(courseId);
        return memberRepository.findByTypeAndAgeGreaterThanAndCoursesId(type, age, courseId).stream().map(memberMapper::toDto).toList();
    }

    private Member findOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    private void validateCourseExists(Long courseId) {
        if (Boolean.FALSE.equals(courseRepository.existsById(courseId))) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
    }

    private Set<Course> resolveCourses(Set<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return new HashSet<>();
        }
        var courses = new HashSet<>(courseRepository.findAllById(courseIds));
        if (courses.size() != courseIds.size()) {
            var foundIds = courses.stream().map(Course::getId).collect(Collectors.toSet());
            var missingIds = courseIds.stream().filter(id -> Boolean.FALSE.equals(foundIds.contains(id))).toList();
            throw new ResourceNotFoundException("Courses not found with ids: " + missingIds);
        }
        return courses;
    }
}
