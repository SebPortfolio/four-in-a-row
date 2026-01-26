package de.paulm.four_in_a_row.game;

public enum Feld {
    LEER(0),
    SPIELER1(1),
    SPIELER2(2);

    private final int wert;

    Feld(int wert) {
        this.wert = wert;
    }

    public int getWert() {
        return wert;
    }

    public static Feld fromWert(int wert) {
        for (Feld feld : values()) {
            if (feld.getWert() == wert) {
                return feld;
            }
        }
        throw new IllegalArgumentException("Ungültiger Wert für Feld: " + wert);
    }
}
