package de.paulm.four_in_a_row.domain.game;

public enum GameStatus {
    IN_PROGRESS, // Spiel läuft gerade, Züge erlaubt
    COMPLETED, // Spiel ist beendet
    PAUSED // Spiel ist pausiert, für Züge gesperrt
}
