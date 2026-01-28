package de.paulm.four_in_a_row.player;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PlayerStatistic {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private PlayerProfile playerProfile;

    private int totalGames;

    private int gamesWon;

    /**
     * Anzahl der Spiele, die der Spieler verloren hat.
     * Nötig, da Unentschieden möglich sind.
     */
    private int gamesLost;

    /**
     * Anzahl der Spiele, die der Spieler aufgegeben hat.
     * In der Anzahl der verlorenen Spiele enthalten.
     */
    private int gamesSurrendered;

    /**
     * Datum, an dem der Spieler zuletzt ein Spiel gespielt hat.
     */
    private LocalDate lastPlayedOn;
}
