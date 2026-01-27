package de.paulm.four_in_a_row.service;

import java.util.List;

import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.ColumnFullException;
import de.paulm.four_in_a_row.domain.exceptions.GameNotFoundException;
import de.paulm.four_in_a_row.game.Game;
import de.paulm.four_in_a_row.game.GameStatus;
import de.paulm.four_in_a_row.profil.PlayerProfile;
import de.paulm.four_in_a_row.repository.GameRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository repository;

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

    @Transactional
    public void applyMove(Long gameId, byte column) {
        Game game = this.getSpielById(gameId);
        byte[][] board = game.getBoard();

        this.dropToken(board, column, game.getCurrentPlayer());
        game.setBoard(board);

        if (this.checkForVictory(board, game.getCurrentPlayer())) {
            game.setStatus(GameStatus.COMPLETED);
        } else {
            this.nextPlayer(game);
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

    private boolean checkForVictory(byte[][] board, byte currentPlayer) {
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

    private void nextPlayer(Game game) {
        byte currentPlayer = game.getCurrentPlayer();
        game.setCurrentPlayer(currentPlayer == 1 ? (byte) 2 : (byte) 1);
    }
}
