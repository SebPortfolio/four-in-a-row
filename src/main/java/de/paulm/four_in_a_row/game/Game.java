package de.paulm.four_in_a_row.game;

import de.paulm.four_in_a_row.profil.PlayerProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.ObjectMapper;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private PlayerProfile player1;

    @OneToOne
    private PlayerProfile player2;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private byte currentPlayer; // 1 oder 2

    @Lob
    @Column(nullable = false)
    private String boardJson;

    private static final byte ROWS = 6;
    private static final byte COLUMNS = 7;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Game(PlayerProfile player1, PlayerProfile player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.status = GameStatus.IN_PROGRESS;
        this.currentPlayer = 1; // Spieler 1 beginnt
        this.boardJson = initBoard();
    }

    // Initiales leeres Spielfeld
    private String initBoard() {
        byte[][] board = new byte[ROWS][COLUMNS];
        return toJson(board);
    }

    // Serialisierung byte[][] -> JSON
    private String toJson(byte[][] board) {
        try {
            return objectMapper.writeValueAsString(board);
        } catch (Exception e) {
            throw new RuntimeException("Board konnte nicht serialisiert werden", e);
        }
    }

    // Deserialisierung JSON -> byte[][]
    private byte[][] fromJson(String json) {
        try {
            return objectMapper.readValue(json, byte[][].class);
        } catch (Exception e) {
            throw new RuntimeException("Board konnte nicht deserialisiert werden", e);
        }
    }

    // Zugriff auf Spielfeld als byte[][] für die Logik
    public byte[][] getBoard() {
        return fromJson(boardJson);
    }

    // Spielfeld setzen + serialisieren
    public void setBoard(byte[][] board) {
        this.boardJson = toJson(board);
    }
}
