package de.paulm.four_in_a_row.web.dtos;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Audit<T> {
    private Long revisionId;
    private LocalDateTime timestamp;
    private Long modifierUserId;
    private String revisionType;
    private T entity;
}
