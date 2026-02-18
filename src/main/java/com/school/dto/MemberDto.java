package com.school.dto;

import com.school.enums.MemberType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    private Long id;
    @NotBlank
    private String name;
    @NotNull
    @Min(1)
    private Integer age;

    @NotBlank
    private String group;

    @NotNull
    private MemberType type;

    private Set<Long> courseIds;
}
