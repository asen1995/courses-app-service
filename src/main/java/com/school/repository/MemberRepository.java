package com.school.repository;

import com.school.entity.Member;
import com.school.enums.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    long countByType(MemberType type);

    List<Member> findByType(MemberType type);

    @Query("SELECT m FROM Member m JOIN m.courses c WHERE m.type = :type AND c.id = :courseId")
    List<Member> findByTypeAndCoursesId(@Param("type") MemberType type, @Param("courseId") Long courseId);

    List<Member> findByGroup(String group);

    @Query("SELECT m FROM Member m JOIN m.courses c WHERE m.type = :type AND m.group = :group AND c.id = :courseId")
    List<Member> findByTypeAndGroupAndCoursesId(@Param("type") MemberType type, @Param("group") String group, @Param("courseId") Long courseId);

    @Query("SELECT m FROM Member m JOIN m.courses c WHERE m.type = :type AND m.age > :age AND c.id = :courseId")
    List<Member> findByTypeAndAgeGreaterThanAndCoursesId(@Param("type") MemberType type, @Param("age") int age, @Param("courseId") Long courseId);
}
