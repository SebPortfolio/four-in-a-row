package de.paulm.four_in_a_row.service;

import static de.paulm.four_in_a_row.repository.specs.GameSpecifications.hasMode;
import static de.paulm.four_in_a_row.repository.specs.GameSpecifications.hasPlayer;
import static de.paulm.four_in_a_row.repository.specs.GameSpecifications.hasStatus;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.ColumnFullException;
import de.paulm.four_in_a_row.domain.exceptions.GameNotFoundException;
import de.paulm.four_in_a_row.domain.game.Game;
import de.paulm.four_in_a_row.domain.game.GameMode;
import de.paulm.four_in_a_row.domain.game.GameResult;
import de.paulm.four_in_a_row.domain.game.GameStatus;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.repository.GameRepository;
import de.paulm.four_in_a_row.repository.specs.GameSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository repository;
    private final PlayerProfileService playerProfileService;
    private final PlayerStatisticService playerStatisticService;

    public Game getGameById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Spiel-ID darf nicht null sein");
        }
        return repository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    /**
     * Sucht nach Spielen basierend auf optionalen Filterkriterien.
     * <p>
     * Diese Methode nutzt die JPA Criteria API (Specifications), um die
     * Datenbankabfrage
     * dynamisch zur Laufzeit aufzubauen. Parameter, die {@code null} sind, werden
     * bei
     * der Filterung ignoriert (entspricht einem "Alle anzeigen"-Filter).
     * </p>
     *
     * @param status   Der zu filternde Spielstatus (z.B. {@code IN_PROGRESS}).
     *                 Bei {@code null} werden Spiele aller Status zurückgegeben.
     * @param mode     Der Spielmodus (z.B. {@code SINGLEPLAYER}).
     *                 Bei {@code null} findet keine Filterung nach Modus statt.
     * @param playerId Die ID eines Spielers. Es wird geprüft, ob dieser Spieler
     *                 entweder
     *                 als {@code player1} oder {@code player2} im Spiel registriert
     *                 ist.
     *                 Bei {@code null} werden Spiele unabhängig von den Teilnehmern
     *                 geladen.
     * @return Eine Liste der gefundenen {@link Game}-Entitäten, die alle Kriterien
     *         erfüllen.
     *         Falls keine Spiele gefunden werden, wird eine leere Liste
     *         zurückgegeben.
     * @see GameSpecifications
     */
    public List<Game> findGames(GameStatus status, GameMode mode, Long playerId) {
        return repository.findAll(
                Specification.<Game>unrestricted()
                        .and(hasStatus(status))
                        .and(hasMode(mode))
                        .and(hasPlayer(playerId)));
    }

    public List<Game> getPausedGamesForPlayer(Long playerId, GameMode gameMode) {
        playerProfileService.getPlayerProfileById(playerId); // sicherstellen, dass der Spieler existiert
        return this.findGames(GameStatus.PAUSED, gameMode, playerId);
    }

    @Transactional
    public Game createGame(Long playerProfileId1, Long playerProfileId2, GameMode gameMode) {
        PlayerProfile player1 = playerProfileService.getPlayerProfileById(playerProfileId1);
        PlayerProfile player2 = playerProfileService.getPlayerProfileById(playerProfileId2);
        Game game = new Game(player1, player2, gameMode);
        return repository.save(game);
    }

    public Game loadGame(Long spielId) {
        // TODO: Logik zum Laden aus einem Speicherort hinzufügen, Spielstatus ändern
        // etc.
        return this.getGameById(spielId);
    }

    @Transactional
    public Game saveGame(Game spiel) {
        if (spiel == null) {
            throw new IllegalArgumentException("Spiel darf nicht null sein");
        }
        return repository.save(spiel);
    }

    @Transactional
    public void deleteGame(Long spielId) {
        if (spielId == null) {
            throw new IllegalArgumentException("Spiel-ID darf nicht null sein");
        }
        repository.deleteById(spielId);
    }

    public void pauseGame(Long spielId) {
        Game game = this.getGameById(spielId);
        game.setStatus(GameStatus.PAUSED);
    }

    public void resumeGame(Long spielId) {
        Game game = this.getGameById(spielId);
        game.setStatus(GameStatus.IN_PROGRESS);
    }

    public void surrenderGame(Long spielId, Long surrenderingPlayerId) {
        Game game = this.getGameById(spielId);
        game.setStatus(GameStatus.COMPLETED);

        // Logik zum Aktualisieren der Spielerstatistiken hinzufügen
    }

    @Transactional
    public Game makeMove(Long gameId, byte column) {
        Game game = this.getGameById(gameId);
        byte[][] board = game.getBoard();

        validateGameState(game);
        validateMove(board, column);

        this.dropToken(board, column, getBoardNumberForCurrentPlayer(game));
        game.setBoard(board);

        if (!checkAndHandleGameEnd(game)) {
            // Spiel geht weiter
            game = this.changeToNextPlayer(game);
        }

        return game;
    }

    private boolean checkAndHandleGameEnd(Game game) {
        byte[][] board = game.getBoard();

        byte boardNumberOfCurrentPlayer = getBoardNumberForCurrentPlayer(game);

        if (this.isVictory(board, boardNumberOfCurrentPlayer)) {
            this.endGameAsVictory(game);
            return true;
        } else if (this.isDraw(board)) {
            this.endGameAsDraw(game);
            return true;
        }
        return false;
    }

    private void endGameAsVictory(Game game) {
        game.setStatus(GameStatus.COMPLETED);
        if (game.getResult() != null) {
            throw new IllegalStateException("Spiel hat bereits ein Ergebnis: " + game.getResult());
        }
        game.setResult(
                game.getCurrentPlayer() == game.getPlayer1() ? GameResult.PLAYER_1_WON : GameResult.PLAYER_2_WON);
        this.updatePlayerStatistics(game, false);
    }

    private void endGameAsDraw(Game game) {
        game.setStatus(GameStatus.COMPLETED);
        if (game.getResult() != null) {
            throw new IllegalStateException("Spiel hat bereits ein Ergebnis: " + game.getResult());
        }
        game.setResult(GameResult.DRAW);
        this.updatePlayerStatistics(game, false);
    }

    public void endGameAsSurrender(Game game, PlayerProfile surrenderingPlayer) { // separat auslösbar
        game.setStatus(GameStatus.COMPLETED);
        if (game.getResult() != null) {
            throw new IllegalStateException("Spiel hat bereits ein Ergebnis: " + game.getResult());
        }

        if (game.getPlayer2().equals(surrenderingPlayer)) {
            game.setResult(GameResult.PLAYER_1_WON);
        } else {
            game.setResult(GameResult.PLAYER_2_WON);
        }

        this.updatePlayerStatistics(game, true);
    }

    private void updatePlayerStatistics(Game game, boolean isSurrender) {
        switch (game.getResult()) {
            case PLAYER_1_WON:
                playerStatisticService.gameWon(game.getPlayer1());
                playerStatisticService.gameLost(game.getPlayer2(), isSurrender);
                break;
            case PLAYER_2_WON:
                playerStatisticService.gameWon(game.getPlayer2());
                playerStatisticService.gameLost(game.getPlayer1(), isSurrender);
                break;
            case DRAW:
                playerStatisticService.gameDrawn(game.getPlayer1());
                playerStatisticService.gameDrawn(game.getPlayer2());
                break;
            default:
                log.warn("Unbekanntes Spielergebnis: " + game.getResult());
                break;
        }
    }

    private void validateGameState(Game game) {
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Spiel ist nicht im Fortschrittsstatus");
        }
    }

    private void validateMove(byte[][] board, byte column) {
        if (column < 0 || column >= board[0].length) {
            throw new IllegalArgumentException("Ungültige Spalte: " + column);
        }
        if (board[0][column] != 0) {
            throw new ColumnFullException(column);
        }
    }

    private void dropToken(byte[][] board, byte column, byte boardNumber) {
        for (int row = board.length - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                board[row][column] = boardNumber;
                return;
            }
        }
        throw new ColumnFullException(column);
    }

    /**
     * Setze Spieler 1 als neuen aktuellen Spieler, wenn Spieler 2 aktueller Spieler
     * ist, und umgekehrt
     * 
     * @param game Das Spiel, für das der aktuelle Spieler gewechselt werden soll
     * @return Das aktualisierte Spiel mit geändertem aktuellem Spieler
     */
    private Game changeToNextPlayer(Game game) {
        game.setCurrentPlayer(game.getCurrentPlayer() == game.getPlayer1() ? game.getPlayer2() : game.getPlayer1());
        return game;
    }

    /**
     * Gibt die Board-Nummer (1 oder 2) zurück, die dem aktuellen Spieler im Spiel
     * zugeordnet ist.
     * 
     * @param game Das Spiel, für das die Board-Nummer ermittelt werden soll
     * @return 1, wenn der aktuelle Spieler Spieler 1 ist, 2 wenn der aktuelle
     *         Spieler Spieler 2 ist
     */
    public byte getBoardNumberForCurrentPlayer(Game game) {
        if (game.getCurrentPlayer().getId().equals(game.getPlayer1().getId()))
            return 1;
        if (game.getCurrentPlayer().getId().equals(game.getPlayer2().getId()))
            return 2;
        throw new IllegalArgumentException("Player not part of this game");
    }

    /**
     * Gibt das Spielerprofil zurück, das der angegebenen Board-Nummer (1 oder 2) im
     * Spiel zugeordnet ist.
     * 
     * @param game   Das Spiel, für das das Spielerprofil ermittelt werden soll
     * @param number Die Board-Nummer (1 oder 2), für die das Spielerprofil
     *               zurückgegeben werden soll
     * @return Das Spielerprofil, das der angegebenen Board-Nummer im Spiel
     *         zugeordnet ist
     */
    public PlayerProfile getPlayerByBoardNumber(Game game, byte number) {
        return (number == 1) ? game.getPlayer1() : game.getPlayer2();
    }

    /**
     * Überprüft, ob der aktuelle Spielstand auf dem Spielfeld ein Sieg für den
     * Spieler mit der angegebenen Board-Nummer darstellt.
     * Ein Sieg liegt vor, wenn der Spieler vier seiner Steine in einer Reihe hat -
     * horizontal, vertikal oder diagonal.
     * 
     * @param board       Das aktuelle Spielfeld, dargestellt als 2D-Array von
     *                    Bytes, wobei 0 für leere Felder, 1 für Spieler 1 und 2 für
     *                    Spieler 2 steht
     * @param boardNumber Die Board-Nummer (1 oder 2) des Spielers, für den
     *                    überprüft werden soll, ob er
     * @return true, wenn der Spieler mit der angegebenen Board-Nummer vier Steine
     *         in einer Reihe hat, andernfalls false
     */
    private boolean isVictory(byte[][] board, byte boardNumber) {
        int totalRows = board.length;
        int totalColumns = board[0].length;

        for (int row = totalRows - 1; row >= 0; row--) {
            for (int column = 0; column < totalColumns; column++) {
                if (board[row][column] != boardNumber) {
                    continue;
                }

                // Horizontal nach rechts
                if (column + 3 < totalColumns &&
                        board[row][column + 1] == boardNumber &&
                        board[row][column + 2] == boardNumber &&
                        board[row][column + 3] == boardNumber) {
                    return true;
                }

                // Vertikal nach oben
                if (row - 3 >= 0 &&
                        board[row - 1][column] == boardNumber &&
                        board[row - 2][column] == boardNumber &&
                        board[row - 3][column] == boardNumber) {
                    return true;
                }

                // Diagonal nach rechts oben
                if (row - 3 >= 0 && column + 3 < totalColumns &&
                        board[row - 1][column + 1] == boardNumber &&
                        board[row - 2][column + 2] == boardNumber &&
                        board[row - 3][column + 3] == boardNumber) {
                    return true;
                }

                // Diagonal nach links oben
                if (row - 3 >= 0 && column - 3 >= 0 &&
                        board[row - 1][column - 1] == boardNumber &&
                        board[row - 2][column - 2] == boardNumber &&
                        board[row - 3][column - 3] == boardNumber) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDraw(byte[][] board) {
        return isBoardFull(board);
    }

    private boolean isBoardFull(byte[][] board) {
        for (int column = 0; column < board[0].length; column++) {
            if (board[0][column] == 0) {
                return false;
            }
        }
        return true;
    }
}
