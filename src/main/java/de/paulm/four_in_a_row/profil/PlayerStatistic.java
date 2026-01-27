package de.paulm.four_in_a_row.profil;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PlayerStatistic {

    @Id
    @GeneratedValue
    private Long id;

    private Long totalGames;

    private Long gamesWon;

    private LocalDate lastPlayedOn;

    public void raiseCountAfterVictory() {
        this.totalGames = this.totalGames + 1;
        this.gamesWon = this.gamesWon + 1;
        this.lastPlayedOn = LocalDate.now();
    }

    public void raiseCountAfterLose() {
        this.totalGames = this.totalGames + 1;
        this.lastPlayedOn = LocalDate.now();
    }
}
