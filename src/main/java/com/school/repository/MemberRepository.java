package com.school.repository;

import com.school.entity.Member;
import com.school.enums.MemberType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Member} entities.
 * <p>
 * Provides custom query methods for filtering members by type, group, course, and age.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Finds a member by ID with courses eagerly loaded.
     *
     * @param id the member ID
     * @return the member, if found
     */
    @EntityGraph(attributePaths = "courses")
    Optional<Member> findWithCoursesById(Long id);

    /**
     * Counts members by their type.
     *
     * @param type the member type
     * @return the number of members matching the type
     */
    long countByType(MemberType type);

    /**
     * Finds all members of a given type.
     *
     * @param type the member type
     * @return list of matching members
     */
    @EntityGraph(attributePaths = "courses")
    List<Member> findByType(MemberType type);

    /**
     * Finds members of a given type enrolled in a specific course.
     *
     * @param type     the member type
     * @param courseId the course ID
     * @return list of matching members
     */
    @EntityGraph(attributePaths = "courses")
    @Query("""
            SELECT m
            FROM Member m
            JOIN m.courses c
            WHERE m.type = :type
              AND c.id = :courseId
            """)
    List<Member> findByTypeAndCoursesId(
            MemberType type,
            Long courseId);

    /**
     * Finds all members belonging to a specific group.
     *
     * @param group the group name
     * @return list of members in the group
     */
    @EntityGraph(attributePaths = "courses")
    List<Member> findByGroup(String group);

    /**
     * Finds members by type, group, and course enrollment.
     *
     * @param type     the member type
     * @param group    the group name
     * @param courseId the course ID
     * @return list of matching members
     */
    @EntityGraph(attributePaths = "courses")
    @Query("""
            SELECT m
            FROM Member m
            JOIN m.courses c
            WHERE m.type = :type
              AND m.group = :group
              AND c.id = :courseId
            """)
    List<Member> findByTypeAndGroupAndCoursesId(
            MemberType type,
            String group,
            Long courseId);

    /**
     * Finds members by type with age greater than or equal to the specified value,
     * enrolled in a course.
     *
     * @param type     the member type
     * @param age      the minimum age (inclusive)
     * @param courseId the course ID
     * @return list of matching members
     */
    @EntityGraph(attributePaths = "courses")
    @Query("""
            SELECT m
            FROM Member m
            JOIN m.courses c
            WHERE m.type = :type
              AND m.age >= :age
              AND c.id = :courseId
            """)
    List<Member> findByTypeAndAgeGreaterThanAndCoursesId(
            MemberType type,
            Integer age,
            Long courseId);
}
