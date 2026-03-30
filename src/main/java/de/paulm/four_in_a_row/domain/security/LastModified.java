package de.paulm.four_in_a_row.domain.security;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass // Sagt JPA, dass dies nur eine Vorlage ist
public abstract class LastModified implements ILastModified {

    @Column(name = "LAST_MODIFIED_AT")
    private LocalDateTime lastModifiedAt;

    @Column(name = "LAST_MODIFIED_BY_USER_ID")
    private Long lastModifiedByUserId;
}