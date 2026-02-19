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

/**
 * Service layer for member management.
 * <p>
 * Handles business logic for creating, retrieving, updating, deleting members,
 * as well as reporting queries such as counting, filtering by type/group/course/age.
 */
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
     * @param courseRepository  the course repository (for course resolution and validation)
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
     * @param dto the member data including course IDs
     * @return the created member
     * @throws ResourceNotFoundException if any course ID is not found
     */
    public MemberDto createMember(MemberDto dto) {
        var member = memberMapper.toMemberEntity(dto);
        member.setCourses(resolveCourses(dto.getCourseIds()));
        return memberMapper.toMemberDto(memberRepository.save(member));
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
        return memberMapper.toMemberDto(findOrThrow(id));
    }

    /**
     * Retrieves all members of a given type.
     *
     * @param type the member type
     * @return list of matching members
     */
    @Transactional(readOnly = true)
    public List<MemberDto> getMembersByType(MemberType type) {
        return memberRepository.findByType(type).stream().map(memberMapper::toMemberDto).toList();
    }

    /**
     * Updates an existing member and its course enrollments.
     *
     * @param id  the member ID
     * @param dto the updated member data
     * @return the updated member
     * @throws ResourceNotFoundException if the member or any course ID is not found
     */
    public MemberDto updateMember(Long id, MemberDto dto) {
        var member = findOrThrow(id);
        memberMapper.updateMemberEntity(dto, member);
        member.setCourses(resolveCourses(dto.getCourseIds()));
        return memberMapper.toMemberDto(memberRepository.save(member));
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
                .stream().map(memberMapper::toMemberDto).toList();
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
                .stream().map(memberMapper::toMemberDto).toList();
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
                .stream().map(memberMapper::toMemberDto).toList();
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
        List<MemberDto> students = findMembersByTypeAndGroupAndCourseId(
                MemberType.STUDENT, group, courseId);
        List<MemberDto> teachers = findMembersByTypeAndGroupAndCourseId(
                MemberType.TEACHER, group, courseId);
        List<MemberDto> allMembers = new ArrayList<>(students);
        allMembers.addAll(teachers);
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
                .stream().map(memberMapper::toMemberDto).toList();
    }

    private Member findOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Member not found with id: %d", id)));
    }

    private void validateCourseExists(Long courseId) {
        if (Boolean.FALSE.equals(courseRepository.existsById(courseId))) {
            throw new ResourceNotFoundException(
                    String.format("Course not found with id: %d", courseId));
        }
    }

    private Set<Course> resolveCourses(Set<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return new HashSet<>();
        }
        var courses = new HashSet<>(courseRepository.findAllById(courseIds));
        if (courses.size() != courseIds.size()) {
            var foundIds = courses.stream().map(Course::getId).collect(Collectors.toSet());
            var missingIds = courseIds.stream()
                    .filter(id -> Boolean.FALSE.equals(foundIds.contains(id)))
                    .toList();
            throw new ResourceNotFoundException(
                    String.format("Courses not found with ids: %s", missingIds));
        }
        return courses;
    }
}
