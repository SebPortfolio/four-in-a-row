package de.paulm.four_in_a_row.domain.security;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PastOrPresent;
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
@Table(name = "USER_SESSION")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId; // bewusst keine Verbindung

    @Column(name = "REFRESH_TOKEN", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "INVALIDATED", nullable = false)
    @Builder.Default
    private boolean invalidated = false;

    @Column(name = "CREATED_AT", nullable = false)
    @PastOrPresent(message = "Erstelldatum darf nicht in der Zukunft liegen")
    private LocalDateTime createdAt;

    @Column(name = "LAST_USED_AT", nullable = false)
    @PastOrPresent(message = "Datum des letzten Gebrauchs darf nicht in der Zukunft liegen")
    private LocalDateTime lastUsedAt;

    @Column(name = "LAST_IP_ADDRESS", nullable = true)
    private InetAddress ipAddress;

    @Column(name = "USER_AGENT", nullable = true)
    private String userAgent;

    public boolean isExpired() {
        LocalDateTime expiresAt = createdAt.plusDays(14); // TODO: über properties festlegen

        Duration duration = Duration.between(createdAt, expiresAt);
        long diff = Math.abs(duration.toSeconds());

        return diff <= 0;
    }

}
