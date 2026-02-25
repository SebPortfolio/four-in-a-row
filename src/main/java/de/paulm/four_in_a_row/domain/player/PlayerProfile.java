package de.paulm.four_in_a_row.domain.player;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "PLAYER_PROFILE")
public class PlayerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", unique = true, nullable = false)
    private Long userId;

    @NotNull(message = "Anzeigename darf nicht null sein")
    @Size(min = 3, message = "Anzeigename muss mindestens 3 Zeichen haben")
    @Pattern(regexp = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){1,28}[a-zA-Z0-9]$", message = "Ungültiger Anzeigename")
    @Column(name = "DISPLAY_NAME", nullable = false, unique = true)
    private String displayName;

    /**
     * Datum der Registrierung des Spielers.
     */
    @NotNull(message = "Registrierungsdatum darf nicht null sein")
    @Column(name = "REGISTERED_ON", nullable = false)
    private LocalDate registeredOn;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PlayerStatistic statistic;
}
