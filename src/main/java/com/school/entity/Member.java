package com.school.entity;

import com.school.enums.MemberType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * JPA entity representing a school member (student or teacher).
 * <p>
 * Members belong to a group and can be enrolled in multiple {@link Course courses}
 * via a many-to-many relationship.
 */
@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "member_group", nullable = false)
    private String group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType type;

    @ManyToMany
    @JoinTable(
        name = "member_courses",
        joinColumns = @JoinColumn(name = "member_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses;
}
