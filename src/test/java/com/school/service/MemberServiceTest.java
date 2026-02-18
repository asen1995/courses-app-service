package com.school.service;

import com.school.dto.MemberDto;
import com.school.entity.Course;
import com.school.entity.Member;
import com.school.enums.MemberType;
import com.school.exception.ResourceNotFoundException;
import com.school.mapper.MemberMapper;
import com.school.repository.CourseRepository;
import com.school.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    @Test
    void shouldCreateMember() {
        var courseId = 1L;
        var course = new Course();
        course.setId(courseId);
        var dto = MemberDto.builder()
                .name("John")
                .age(20)
                .group("A1")
                .type(MemberType.STUDENT)
                .courseIds(Set.of(courseId))
                .build();
        var entity = new Member();
        var savedEntity = new Member();
        savedEntity.setId(1L);
        var expectedDto = MemberDto.builder()
                .id(1L)
                .name("John")
                .age(20)
                .group("A1")
                .type(MemberType.STUDENT)
                .courseIds(Set.of(courseId))
                .build();

        when(memberMapper.toEntity(dto)).thenReturn(entity);
        when(courseRepository.findAllById(Set.of(courseId))).thenReturn(List.of(course));
        when(memberRepository.save(entity)).thenReturn(savedEntity);
        when(memberMapper.toDto(savedEntity)).thenReturn(expectedDto);

        var result = memberService.createMember(dto);

        assertThat(result).isEqualTo(expectedDto);
        assertThat(entity.getCourses()).containsExactly(course);
        verify(memberMapper).toEntity(dto);
        verify(memberRepository).save(entity);
    }

    @Test
    void shouldCreateMemberWithNoCourses() {
        var dto = MemberDto.builder()
                .name("John")
                .age(20)
                .group("A1")
                .type(MemberType.STUDENT)
                .courseIds(Set.of())
                .build();
        var entity = new Member();
        var savedEntity = new Member();
        var expectedDto = MemberDto.builder().id(1L).name("John").build();

        when(memberMapper.toEntity(dto)).thenReturn(entity);
        when(memberRepository.save(entity)).thenReturn(savedEntity);
        when(memberMapper.toDto(savedEntity)).thenReturn(expectedDto);

        var result = memberService.createMember(dto);

        assertThat(result).isEqualTo(expectedDto);
        assertThat(entity.getCourses()).isEmpty();
    }

    @Test
    void shouldCreateMemberWithNullCourseIds() {
        var dto = MemberDto.builder()
                .name("John")
                .age(20)
                .group("A1")
                .type(MemberType.STUDENT)
                .courseIds(null)
                .build();
        var entity = new Member();
        var savedEntity = new Member();
        var expectedDto = MemberDto.builder().id(1L).name("John").build();

        when(memberMapper.toEntity(dto)).thenReturn(entity);
        when(memberRepository.save(entity)).thenReturn(savedEntity);
        when(memberMapper.toDto(savedEntity)).thenReturn(expectedDto);

        var result = memberService.createMember(dto);

        assertThat(result).isEqualTo(expectedDto);
        assertThat(entity.getCourses()).isEmpty();
    }

    @Test
    void shouldGetMemberById() {
        var entity = new Member();
        entity.setId(1L);
        var expectedDto = MemberDto.builder()
                .id(1L)
                .name("John")
                .type(MemberType.STUDENT)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(memberMapper.toDto(entity)).thenReturn(expectedDto);

        var result = memberService.getMemberById(1L);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void shouldThrowWhenMemberNotFoundById() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: 999");
    }

    @Test
    void shouldGetMembersByType() {
        var entity1 = new Member();
        var entity2 = new Member();
        var dto1 = MemberDto.builder().id(1L).name("John").type(MemberType.STUDENT).build();
        var dto2 = MemberDto.builder().id(2L).name("Jane").type(MemberType.STUDENT).build();

        when(memberRepository.findByType(MemberType.STUDENT)).thenReturn(List.of(entity1, entity2));
        when(memberMapper.toDto(entity1)).thenReturn(dto1);
        when(memberMapper.toDto(entity2)).thenReturn(dto2);

        var result = memberService.getMembersByType(MemberType.STUDENT);

        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    void shouldUpdateMember() {
        var courseId = 2L;
        var course = new Course();
        course.setId(courseId);
        var entity = new Member();
        entity.setId(1L);
        var dto = MemberDto.builder()
                .name("John Updated")
                .age(21)
                .group("B1")
                .type(MemberType.STUDENT)
                .courseIds(Set.of(courseId))
                .build();
        var savedEntity = new Member();
        var expectedDto = MemberDto.builder()
                .id(1L)
                .name("John Updated")
                .age(21)
                .group("B1")
                .type(MemberType.STUDENT)
                .courseIds(Set.of(courseId))
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(courseRepository.findAllById(Set.of(courseId))).thenReturn(List.of(course));
        when(memberRepository.save(entity)).thenReturn(savedEntity);
        when(memberMapper.toDto(savedEntity)).thenReturn(expectedDto);

        var result = memberService.updateMember(1L, dto);

        assertThat(result).isEqualTo(expectedDto);
        verify(memberMapper).updateEntity(dto, entity);
        assertThat(entity.getCourses()).containsExactly(course);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentMember() {
        var dto = MemberDto.builder().name("John").age(20).group("A1").type(MemberType.STUDENT).build();

        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.updateMember(999L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: 999");
    }

    @Test
    void shouldDeleteMember() {
        when(memberRepository.existsById(1L)).thenReturn(true);

        memberService.deleteMember(1L);

        verify(memberRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentMember() {
        when(memberRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> memberService.deleteMember(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: 999");
    }

    @Test
    void shouldCountMembersByType() {
        when(memberRepository.countByType(MemberType.STUDENT)).thenReturn(5L);

        var result = memberService.countMembersByType(MemberType.STUDENT);

        assertThat(result).isEqualTo(5L);
    }

    @Test
    void shouldFindMembersByTypeAndCourseId() {
        var entity = new Member();
        var expectedDto = MemberDto.builder().id(1L).name("John").type(MemberType.STUDENT).build();

        when(memberRepository.findByTypeAndCoursesId(MemberType.STUDENT, 1L)).thenReturn(List.of(entity));
        when(memberMapper.toDto(entity)).thenReturn(expectedDto);

        var result = memberService.findMembersByTypeAndCourseId(MemberType.STUDENT, 1L);

        assertThat(result).containsExactly(expectedDto);
    }

    @Test
    void shouldFindMembersByGroup() {
        var entity = new Member();
        var expectedDto = MemberDto.builder().id(1L).name("John").group("A1").build();

        when(memberRepository.findByGroup("A1")).thenReturn(List.of(entity));
        when(memberMapper.toDto(entity)).thenReturn(expectedDto);

        var result = memberService.findMembersByGroup("A1");

        assertThat(result).containsExactly(expectedDto);
    }

    @Test
    void shouldFindMembersByTypeAndGroupAndCourseId() {
        var entity = new Member();
        var expectedDto = MemberDto.builder().id(1L).name("John").type(MemberType.STUDENT).group("A1").build();

        when(memberRepository.findByTypeAndGroupAndCoursesId(MemberType.STUDENT, "A1", 1L)).thenReturn(List.of(entity));
        when(memberMapper.toDto(entity)).thenReturn(expectedDto);

        var result = memberService.findMembersByTypeAndGroupAndCourseId(MemberType.STUDENT, "A1", 1L);

        assertThat(result).containsExactly(expectedDto);
    }

    @Test
    void shouldFindMembersByTypeAndAgeGreaterThanAndCourseId() {
        var entity = new Member();
        var expectedDto = MemberDto.builder().id(1L).name("Jane").age(22).type(MemberType.STUDENT).build();

        when(memberRepository.findByTypeAndAgeGreaterThanAndCoursesId(MemberType.STUDENT, 20, 1L)).thenReturn(List.of(entity));
        when(memberMapper.toDto(entity)).thenReturn(expectedDto);

        var result = memberService.findMembersByTypeAndAgeGreaterThanAndCourseId(MemberType.STUDENT, 20, 1L);

        assertThat(result).containsExactly(expectedDto);
    }
}
