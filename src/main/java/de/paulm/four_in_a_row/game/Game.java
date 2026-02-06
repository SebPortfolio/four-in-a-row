package de.paulm.four_in_a_row.game;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import de.paulm.four_in_a_row.player.PlayerProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "GAME")
public class Game {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "PLAYER_1_ID", nullable = false)
    private PlayerProfile player1;

    @OneToOne
    @JoinColumn(name = "PLAYER_2_ID", nullable = false)
    private PlayerProfile player2;

    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_STATUS", nullable = false)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_RESULT", nullable = true)
    private GameResult result;

    @ManyToOne
    @JoinColumn(name = "CURRENT_PLAYER_ID", nullable = false)
    private PlayerProfile currentPlayer;

    /**
     * Spielfeld als 2D-Array, wobei 0 = leer, 1 = Spieler 1, 2 = Spieler 2.
     * Wird als JSON in der Datenbank gespeichert.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "BOARD")
    private byte[][] board;

    @Transient
    private static final byte ROWS = 6;
    @Transient
    private static final byte COLUMNS = 7;

    public Game(PlayerProfile player1, PlayerProfile player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.status = GameStatus.IN_PROGRESS;
        this.currentPlayer = player1; // Spieler 1 beginnt
        this.board = new byte[ROWS][COLUMNS]; // Initialisiere leeres Spielfeld
    }
}
