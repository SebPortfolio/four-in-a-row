package de.paulm.four_in_a_row.domain.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Field {
    EMPTY(0),
    PLAYER1(1),
    PLAYER2(2);

    private final int value;

    public static Field fromValue(int value) {
        for (Field field : values()) {
            if (field.getValue() == value) {
                return field;
            }
        }
        throw new IllegalArgumentException("Ungültiger Wert für Feld: " + value);
    }
}
