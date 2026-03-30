package de.paulm.four_in_a_row.domain.security;

import java.time.LocalDateTime;

public interface ILastModified {
    LocalDateTime getLastModifiedAt();

    void setLastModifiedAt(LocalDateTime at);

    Long getLastModifiedByUserId();

    void setLastModifiedByUserId(Long id);
}
