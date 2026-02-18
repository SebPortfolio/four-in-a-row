package de.paulm.four_in_a_row.domain.player;

import java.time.LocalDate;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PLAYER_STATISTIC")
@Check(constraints = "GAMES_LOST >= GAMES_SURRENDERED AND TOTAL_GAMES >= (GAMES_WON + GAMES_LOST)")
public class PlayerStatistic {

    @Id
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Koppelt diese ID an die ID von playerProfile
    @JoinColumn(name = "ID")
    private PlayerProfile profile;

    /**
     * Anzahl aller gespielten Spiele.
     */
    @Min(value = 0, message = "Anzahl der gespielten Spiele darf nicht negativ sein")
    @Column(name = "TOTAL_GAMES", nullable = false)
    @Check(constraints = "TOTAL_GAMES >= 0")
    private int totalGames;

    /**
     * Anzahl der Spiele, die der Spieler gewonnen hat.
     */
    @Min(value = 0, message = "Anzahl der gewonnenen Spiele darf nicht negativ sein")
    @Column(name = "GAMES_WON", nullable = false)
    @Check(constraints = "GAMES_WON >= 0")
    private int gamesWon;

    /**
     * Anzahl der Spiele, die der Spieler verloren hat.
     */
    @Min(value = 0, message = "Anzahl der verlorenen Spiele darf nicht negativ sein")
    @Column(name = "GAMES_LOST", nullable = false)
    @Check(constraints = "GAMES_LOST >= 0")
    private int gamesLost;

    /**
     * Anzahl der Spiele, die der Spieler aufgegeben hat.
     * In der Anzahl der verlorenen Spiele enthalten.
     */
    @Min(value = 0, message = "Anzahl der aufgegebenen Spiele darf nicht negativ sein")
    @Column(name = "GAMES_SURRENDERED", nullable = false)
    @Check(constraints = "GAMES_SURRENDERED >= 0")
    private int gamesSurrendered;

    /**
     * Datum, an dem der Spieler zuletzt ein Spiel gespielt hat.
     */
    @PastOrPresent(message = "Datum des letzten Spiels darf nicht in der Zukunft liegen")
    private LocalDate lastPlayedOn;

    // Immer wenn man repository.save() aufruft -> Spring Boot erkennt Methoden,
    // die mit "is" beginnen und @AssertTrue haben, automatisch als Validierung.
    @AssertTrue(message = "Aufgegebene Spiele können nicht häufiger sein als verlorene Spiele")
    public boolean isSurrenderLogicValid() {
        return gamesSurrendered <= gamesLost;
    }

    @AssertTrue(message = "Die Summe der Siege und Niederlagen darf die Gesamtzahl nicht überschreiten")
    public boolean isTotalGamesLogicValid() {
        return (gamesWon + gamesLost) <= totalGames;
    }
}
