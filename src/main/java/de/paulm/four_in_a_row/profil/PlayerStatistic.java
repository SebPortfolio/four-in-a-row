package de.paulm.four_in_a_row.profil;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PlayerStatistic {

    @Id
    @GeneratedValue
    private Long id;

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
