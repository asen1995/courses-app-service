package com.school.entity;

import com.school.enums.CourseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * JPA entity representing a school course.
 * <p>
 * Courses have a type (MAIN or SECONDARY) and can have multiple {@link Member members}
 * enrolled via a many-to-many relationship.
 */
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseType type;

    @ManyToMany(mappedBy = "courses")
    private Set<Member> members;
}
