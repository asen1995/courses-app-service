package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data transfer object wrapping a count value.
 * <p>
 * Used by reporting endpoints to return entity counts.
 */
@Getter
@AllArgsConstructor
public class CountDto {
    private Long count;
}
