package de.paulm.four_in_a_row.domain.security;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BAN_ACTION")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BAN_ID", nullable = false)
    private Ban ban;

    @Column(name = "EXECUTING_USER_ID")
    private Long executingUserId;

    @Column(name = "TIME_STAMP")
    @Builder.Default
    private LocalDateTime timeStamp = LocalDateTime.now();

    @Column(name = "COMMENT", length = 512)
    private String comment;

    @Column(name = "OLD_VALUE")
    private String oldValue;

    @Column(name = "NEW_VALUE")
    private String newValue;
}