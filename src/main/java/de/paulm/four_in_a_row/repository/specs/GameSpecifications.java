package de.paulm.four_in_a_row.repository.specs;

import org.springframework.data.jpa.domain.Specification;

import de.paulm.four_in_a_row.domain.game.Game;
import de.paulm.four_in_a_row.domain.game.GameMode;
import de.paulm.four_in_a_row.domain.game.GameStatus;

/**
 * Factory-Klasse für JPA-Specifications zur Abfrage von {@link Game}-Entitäten.
 * <p>
 * Diese Klasse stellt wiederverwendbare Filter-Bausteine bereit, die mittels
 * der
 * JPA Criteria API dynamische SQL-Prädikate erzeugen. Alle Methoden sind so
 * implementiert,
 * dass sie bei {@code null}-Eingaben ein neutrales Prädikat (Conjunction)
 * zurückgeben,
 * welches die Gesamtabfrage nicht einschränkt.
 * </p>
 */
public class GameSpecifications {
    /**
     * Erzeugt ein Prädikat zur Filterung nach dem Spielstatus.
     *
     * @param status Der gesuchte {@link GameStatus}. Bei {@code null} wird keine
     *               Einschränkung vorgenommen.
     * @return Eine Specification, die das Attribut {@code status} mit dem
     *         übergebenen Wert vergleicht.
     */
    public static Specification<Game> hasStatus(GameStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    /**
     * Erzeugt ein Prädikat zur Filterung nach dem Spielmodus.
     *
     * @param mode Der gesuchte {@link GameMode}. Bei {@code null} wird keine
     *             Einschränkung vorgenommen.
     * @return Eine Specification, die das Attribut {@code gameMode} mit dem
     *         übergebenen Wert vergleicht.
     */
    public static Specification<Game> hasMode(GameMode mode) {
        return (root, query, cb) -> mode == null ? cb.conjunction() : cb.equal(root.get("gameMode"), mode);
    }

    /**
     * Erzeugt ein Prädikat, das prüft, ob ein bestimmter Spieler am Spiel beteiligt
     * ist.
     * <p>
     * Es wird eine ODER-Verknüpfung (Disjunction) erstellt, die prüft, ob die
     * übergebene
     * Player-ID entweder mit der ID von {@code player1} oder {@code player2}
     * übereinstimmt.
     * </p>
     *
     * @param playerId Die ID des Spielers. Bei {@code null} wird keine
     *                 Einschränkung vorgenommen.
     * @return Eine Specification für eine ODER-Abfrage über die Join-Attribute
     *         {@code player1.id} und {@code player2.id}.
     */
    public static Specification<Game> hasPlayer(Long playerId) {
        return (root, query, cb) -> {
            if (playerId == null)
                return cb.conjunction();
            // Erzeugt: WHERE player1_id = ? OR player2_id = ?
            return cb.or(
                    cb.equal(root.get("player1").get("id"), playerId),
                    cb.equal(root.get("player2").get("id"), playerId));
        };
    }
}
