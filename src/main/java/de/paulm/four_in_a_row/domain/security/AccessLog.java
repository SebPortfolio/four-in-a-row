package de.paulm.four_in_a_row.domain.security;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACCESS_LOG")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "EXECUTING_USER_ID", nullable = false)
    private Long executingUserId;
    @Column(name = "TARGET_USER_ID", nullable = false)
    private Long targetUserId;
    @Column(name = "ACTION", nullable = false)
    private String action; // bspw. "REVEAL_EMAIL"
    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;
}
