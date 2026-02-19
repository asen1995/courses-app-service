package com.school.service;

import com.school.dto.CountDto;
import com.school.dto.GroupCourseReportDto;
import com.school.dto.MemberDto;
import com.school.entity.Course;
import com.school.entity.Member;
import com.school.enums.MemberType;
import com.school.exception.DuplicateTeacherException;
import com.school.exception.ResourceNotFoundException;
import com.school.mapper.MemberMapper;
import com.school.repository.CourseRepository;
import com.school.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for member management.
 * <p>
 * Handles business logic for creating, retrieving, updating, deleting members,
 * as well as reporting queries such as counting, filtering by type/group/course/age.
 */
@Slf4j
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final MemberMapper memberMapper;

    /**
     * Constructs the service with the required dependencies.
     *
     * @param memberRepository the member repository
     * @param courseRepository the course repository (for course resolution and validation)
     * @param memberMapper     the member mapper
     */
    public MemberService(MemberRepository memberRepository,
                         CourseRepository courseRepository,
                         MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository;
        this.memberMapper = memberMapper;
    }

    /**
     * Creates a new member with the specified course enrollments.
     *
     * @param memberDto the member data including course IDs
     * @return the created member
     * @throws ResourceNotFoundException if any course ID is not found
     */
    public MemberDto createMember(MemberDto memberDto) {
        if (memberDto.isTeacher() && memberDto.isAssignedToCourses()) {
            validateOneTeacherPerCourse(memberDto.getCourseIds(), null);
        }
        Member member = memberMapper.toMemberEntity(memberDto);
        member.setCourses(memberDto.isAssignedToCourses() ? resolveCourses(memberDto.getCourseIds()) : new HashSet<>());
        Member savedMember = memberRepository.save(member);
        log.info("Created member with id: {}", savedMember.getId());
        return memberMapper.toMemberDto(savedMember);
    }

    /**
     * Retrieves a member by its ID.
     *
     * @param id the member ID
     * @return the member data
     * @throws ResourceNotFoundException if the member is not found
     */
    @Transactional(readOnly = true)
    public MemberDto getMemberById(Long id) {
        return memberMapper.toMemberDto(findMemberWithCoursesById(id));
    }

    /**
     * Retrieves all members of a given type.
     *
     * @param type the member type
     * @return list of matching members
     */
    @Transactional(readOnly = true)
    public List<MemberDto> getMembersByType(MemberType type) {
        return memberRepository.findByType(type)
                .stream()
                .map(memberMapper::toMemberDto)
                .toList();
    }

    /**
     * Updates an existing member and its course enrollments.
     *
     * @param id        the member ID
     * @param memberDto the updated member data
     * @return the updated member
     * @throws ResourceNotFoundException if the member or any course ID is not found
     */
    public MemberDto updateMember(Long id, MemberDto memberDto) {
        Member member = findMemberById(id);
        if (memberDto.isTeacher() && memberDto.isAssignedToCourses()) {
            validateOneTeacherPerCourse(memberDto.getCourseIds(), id);
        }
        memberMapper.updateMemberEntity(memberDto, member);
        member.setCourses(memberDto.isAssignedToCourses() ? resolveCourses(memberDto.getCourseIds()) : new HashSet<>());
        Member savedMember = memberRepository.save(member);
        log.info("Updated member with id: {}", savedMember.getId());
        return memberMapper.toMemberDto(savedMember);
    }

    /**
     * Deletes a member by its ID.
     *
     * @param id the member ID
     * @throws ResourceNotFoundException if the member is not found
     */
    public void deleteMember(Long id) {
        if (Boolean.FALSE.equals(memberRepository.existsById(id))) {
            throw new ResourceNotFoundException(
                    String.format("Member not found with id: %d", id));
        }
        memberRepository.deleteById(id);
    }

    /**
     * Counts members by type.
     *
     * @param type the member type
     * @return the count wrapped in a DTO
     */
    @Transactional(readOnly = true)
    public CountDto countMembersByType(MemberType type) {
        return new CountDto(memberRepository.countByType(type));
    }

    /**
     * Finds members of a given type enrolled in a specific course.
     *
     * @param type     the member type
     * @param courseId the course ID
     * @return list of matching members
     * @throws ResourceNotFoundException if the course is not found
     */
    @Transactional(readOnly = true)
    public List<MemberDto> findMembersByTypeAndCourseId(MemberType type, Long courseId) {
        validateCourseExists(courseId);
        return memberRepository.findByTypeAndCoursesId(type, courseId)
                .stream()
                .map(memberMapper::toMemberDto)
                .toList();
    }

    /**
     * Finds all members belonging to a specific group.
     *
     * @param group the group name
     * @return list of members in the group
     */
    @Transactional(readOnly = true)
    public List<MemberDto> findMembersByGroup(String group) {
        return memberRepository.findByGroup(group)
                .stream()
                .map(memberMapper::toMemberDto)
                .toList();
    }

    /**
     * Finds members by type, group, and course enrollment.
     *
     * @param type     the member type
     * @param group    the group name
     * @param courseId the course ID
     * @return list of matching members
     */
    @Transactional(readOnly = true)
    public List<MemberDto> findMembersByTypeAndGroupAndCourseId(
            MemberType type, String group, Long courseId) {
        return memberRepository.findByTypeAndGroupAndCoursesId(type, group, courseId)
                .stream()
                .map(memberMapper::toMemberDto)
                .toList();
    }

    /**
     * Builds a report of members in a group enrolled in a specific course,
     * combining both students and teachers.
     *
     * @param group    the group name
     * @param courseId the course ID
     * @return the group-course report
     * @throws ResourceNotFoundException if the course is not found
     */
    @Transactional(readOnly = true)
    public GroupCourseReportDto findMembersByGroupAndCourseId(String group, Long courseId) {
        validateCourseExists(courseId);
        List<MemberDto> allMembers = memberRepository.findByTypesAndGroupAndCoursesId(
                        List.of(MemberType.STUDENT, MemberType.TEACHER), group, courseId)
                .stream()
                .map(memberMapper::toMemberDto)
                .toList();
        if (allMembers.isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format("No members found for group: %s and course id: %d", group, courseId));
        }
        return GroupCourseReportDto.builder()
                .group(group)
                .courseId(courseId)
                .members(allMembers)
                .build();
    }

    /**
     * Finds members by type with age greater than the specified value, enrolled in a course.
     *
     * @param type     the member type
     * @param age      the minimum age (inclusive)
     * @param courseId the course ID
     * @return list of matching members
     * @throws ResourceNotFoundException if the course is not found
     */
    @Transactional(readOnly = true)
    public List<MemberDto> findMembersByTypeAndAgeGreaterThanAndCourseId(
            MemberType type, Integer age, Long courseId) {
        validateCourseExists(courseId);
        return memberRepository.findByTypeAndAgeGreaterThanAndCoursesId(type, age, courseId)
                .stream()
                .map(memberMapper::toMemberDto)
                .toList();
    }

    /**
     * Ensures each course has at most one teacher.
     * On create, {@code currentMemberId} is {@code null} so any existing teacher is a conflict.
     * On update, the member's own ID is passed so it doesn't conflict with itself.
     */
    private void validateOneTeacherPerCourse(Set<Long> courseIds, Long currentMemberId) {
        for (Long courseId : courseIds) {
            boolean teacherExists = memberRepository.courseHasAnotherTeacher(
                    courseId, currentMemberId);
            if (teacherExists) {
                throw new DuplicateTeacherException(
                        String.format("A teacher is already assigned to course with id: %d", courseId));
            }
        }
    }

    /** Finds a member by ID or throws {@link ResourceNotFoundException}. */
    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Member not found with id: %d", id)));
    }

    /** Finds a member by ID with eagerly loaded courses or throws {@link ResourceNotFoundException}. */
    private Member findMemberWithCoursesById(Long id) {
        return memberRepository.findWithCoursesById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Member not found with id: %d", id)));
    }

    /** Validates that a course exists or throws {@link ResourceNotFoundException}. */
    private void validateCourseExists(Long courseId) {
        if (Boolean.FALSE.equals(courseRepository.existsById(courseId))) {
            throw new ResourceNotFoundException(
                    String.format("Course not found with id: %d", courseId));
        }
    }

    /** Resolves course IDs to entities or throws {@link ResourceNotFoundException} for missing IDs. */
    private Set<Course> resolveCourses(Set<Long> courseIds) {
        Set<Course> courses = new HashSet<>(courseRepository.findAllById(courseIds));
        if (courses.size() != courseIds.size()) {
            Set<Long> foundIds = courses.stream().map(Course::getId).collect(Collectors.toSet());
            List<Long> missingIds = courseIds.stream()
                    .filter(id -> Boolean.FALSE.equals(foundIds.contains(id)))
                    .toList();
            throw new ResourceNotFoundException(
                    String.format("Courses not found with ids: %s", missingIds));
        }
        return courses;
    }
}
