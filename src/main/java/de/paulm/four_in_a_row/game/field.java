package de.paulm.four_in_a_row.game;

public enum field {
    EMPTY(0),
    PLAYER1(1),
    PLAYER2(2);

    private final int value;

    field(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static field fromValue(int value) {
        for (field field : values()) {
            if (field.getValue() == value) {
                return field;
            }
        }
        throw new IllegalArgumentException("Ungültiger Wert für Feld: " + value);
    }
}
