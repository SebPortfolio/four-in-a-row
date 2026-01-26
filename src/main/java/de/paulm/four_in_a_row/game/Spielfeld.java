package de.paulm.four_in_a_row.game;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Spielfeld {

    @Id
    @GeneratedValue
    private Long id;

    private static final byte REIHEN = 6;
    private static final byte SPALTEN = 7;

    @OneToOne(mappedBy = "spielfeld")
    private Spiel spiel;

    @Lob
    private String boardJson;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Spielfeld(Spiel spiel) {
        this.spiel = spiel;
        this.boardJson = initBoard();
    }

    // Initiales leeres Board
    private String initBoard() {
        byte[][] board = new byte[REIHEN][SPALTEN];
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

    // Zugriff auf Board als byte[][] für die Logik
    public byte[][] getBoard() {
        return fromJson(boardJson);
    }

    // Board setzen + serialisieren
    public void setBoard(byte[][] board) {
        this.boardJson = toJson(board);
    }
}
