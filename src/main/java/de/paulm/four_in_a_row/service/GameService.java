package de.paulm.four_in_a_row.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.ColumnFullException;
import de.paulm.four_in_a_row.domain.exceptions.GameNotFoundException;
import de.paulm.four_in_a_row.game.Game;
import de.paulm.four_in_a_row.game.GameResult;
import de.paulm.four_in_a_row.game.GameStatus;
import de.paulm.four_in_a_row.profil.PlayerProfile;
import de.paulm.four_in_a_row.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository repository;
    private final PlayerStatisticService playerStatisticService;

    public Game getSpielById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public List<Game> getPausedGamesForPlayer(Long playerId) {
        return repository.findAllByStatusAndPlayer1OrPlayer2(GameStatus.PAUSED, playerId, playerId);
    }

    public Game createGame(PlayerProfile player1, PlayerProfile player2) {
        Game game = new Game(player1, player2);
        return repository.save(game);
    }

    public Game ladeSpiel(Long spielId) {
        // TODO: Logik zum Laden aus einem Speicherort hinzufügen, Spielstatus ändern
        // etc.
        return this.getSpielById(spielId);
    }

    public Game saveGame(Game spiel) {
        return repository.save(spiel);
    }

    public void deleteGame(Long spielId) {
        repository.deleteById(spielId);
    }

    public void pauseGame(Long spielId) {
        Game game = this.getSpielById(spielId);
        game.setStatus(GameStatus.PAUSED);
    }

    public void resumeGame(Long spielId) {
        Game game = this.getSpielById(spielId);
        game.setStatus(GameStatus.IN_PROGRESS);
    }

    public void surrenderGame(Long spielId, Long surrenderingPlayerId) {
        Game game = this.getSpielById(spielId);
        game.setStatus(GameStatus.COMPLETED);

        // Logik zum Aktualisieren der Spielerstatistiken hinzufügen
    }

    @Transactional
    public void applyMove(Long gameId, byte column) {
        Game game = this.getSpielById(gameId);
        byte[][] board = game.getBoard();

        validateGameState(game);
        validateMove(board, column);

        this.dropToken(board, column, game.getCurrentPlayer());
        game.setBoard(board);

        if (!checkAndHandleGameEnd(game)) {
            // Spiel geht weiter
            this.nextPlayer(game);
        }
    }

    private boolean checkAndHandleGameEnd(Game game) {
        byte[][] board = game.getBoard();

        if (this.isVictory(board, game.getCurrentPlayer())) {
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
        game.setResult(game.getCurrentPlayer() == 1 ? GameResult.PLAYER1_WON : GameResult.PLAYER2_WON);
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
            game.setResult(GameResult.PLAYER1_WON);
        } else {
            game.setResult(GameResult.PLAYER2_WON);
        }

        this.updatePlayerStatistics(game, true);
    }

    private void updatePlayerStatistics(Game game, boolean isSurrender) {
        switch (game.getResult()) {
            case PLAYER1_WON:
                playerStatisticService.gameWon(game.getPlayer1());
                playerStatisticService.gameLost(game.getPlayer2(), isSurrender);
                break;
            case PLAYER2_WON:
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

    /*
     * private PlayerProfile getCurrentPlayerProfile(Game game) {
     * byte currentPlayer = game.getCurrentPlayer();
     * 
     * if (currentPlayer == 1) {
     * return game.getPlayer1();
     * } else if (currentPlayer == 2) {
     * return game.getPlayer2();
     * }
     * 
     * throw new IllegalStateException("Ungültiger aktueller Spieler: " +
     * currentPlayer);
     * }
     * 
     * private PlayerProfile getNotCurrentPlayerProfile(Game game) {
     * byte currentPlayer = game.getCurrentPlayer();
     * 
     * if (currentPlayer == 1) {
     * return game.getPlayer2();
     * } else if (currentPlayer == 2) {
     * return game.getPlayer1();
     * }
     * 
     * throw new IllegalStateException("Ungültiger aktueller Spieler: " +
     * currentPlayer);
     * }
     */

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

    private void dropToken(byte[][] board, byte column, byte currentPlayer) {
        for (int row = board.length - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                board[row][column] = currentPlayer;
                return;
            }
        }
        throw new ColumnFullException(column);
    }

    private void nextPlayer(Game game) {
        byte currentPlayer = game.getCurrentPlayer();
        game.setCurrentPlayer(currentPlayer == 1 ? (byte) 2 : (byte) 1);
    }

    private boolean isVictory(byte[][] board, byte currentPlayer) {
        int totalRows = board.length;
        int totalColumns = board[0].length;

        for (int row = totalRows - 1; row >= 0; row--) {
            for (int column = 0; column < totalColumns; column++) {
                if (board[row][column] != currentPlayer) {
                    continue;
                }

                // Horizontal nach rechts
                if (column + 3 < totalColumns &&
                        board[row][column + 1] == currentPlayer &&
                        board[row][column + 2] == currentPlayer &&
                        board[row][column + 3] == currentPlayer) {
                    return true;
                }

                // Vertikal nach oben
                if (row - 3 >= 0 &&
                        board[row - 1][column] == currentPlayer &&
                        board[row - 2][column] == currentPlayer &&
                        board[row - 3][column] == currentPlayer) {
                    return true;
                }

                // Diagonal nach rechts oben
                if (row - 3 >= 0 && column + 3 < totalColumns &&
                        board[row - 1][column + 1] == currentPlayer &&
                        board[row - 2][column + 2] == currentPlayer &&
                        board[row - 3][column + 3] == currentPlayer) {
                    return true;
                }

                // Diagonal nach links oben
                if (row - 3 >= 0 && column - 3 >= 0 &&
                        board[row - 1][column - 1] == currentPlayer &&
                        board[row - 2][column - 2] == currentPlayer &&
                        board[row - 3][column - 3] == currentPlayer) {
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
