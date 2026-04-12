package de.paulm.four_in_a_row.domain.game;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "GAME")
@Builder
public class Game {

    public static final int ROWS = 6;
    public static final int COLUMNS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PLAYER_1_ID", nullable = false)
    private PlayerProfile player1;

    @ManyToOne
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
    private int[][] board;

    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_MODE", nullable = false)
    private GameMode mode;
}
