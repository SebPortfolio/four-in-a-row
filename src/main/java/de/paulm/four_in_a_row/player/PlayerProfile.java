package de.paulm.four_in_a_row.player;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PlayerProfile {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @NotNull(message = "Benutzername darf nicht null sein")
    @Size(min = 3, message = "Benutzername muss mindestens 3 Zeichen haben")
    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @NotNull(message = "E-Mail darf nicht null sein")
    @Size(min = 5, message = "E-Mail muss mindestens 5 Zeichen haben")
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    /**
     * Datum der Registrierung des Spielers.
     */
    @NotNull(message = "Registrierungsdatum darf nicht null sein")
    @Column(name = "REGISTERED_ON", nullable = false)
    private LocalDate registeredOn;

    @OneToOne(mappedBy = "playerProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PlayerStatistic statistic;
}
