package de.paulm.four_in_a_row.web.handler;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.GameApiDelegate;
import de.paulm.four_in_a_row.game.Game;
import de.paulm.four_in_a_row.game.GameMode;
import de.paulm.four_in_a_row.game.GameStatus;
import de.paulm.four_in_a_row.mapper.GameMapper;
import de.paulm.four_in_a_row.mapper.GameModeMapper;
import de.paulm.four_in_a_row.mapper.GameStatusMapper;
import de.paulm.four_in_a_row.service.GameService;
import de.paulm.four_in_a_row.web.util.ResourceLocationHelper;
import de.paulm.model.GameCreateRequestWdto;
import de.paulm.model.GameModeWdto;
import de.paulm.model.GameStatusWdto;
import de.paulm.model.GameWdto;
import de.paulm.model.MoveRequestWdto;

@RestController
public class GameApiHandler implements GameApiDelegate {

    private final GameService gameService;
    private final GameMapper gameMapper;
    private final GameModeMapper gameModeMapper;
    private final GameStatusMapper gameStatusMapper;

    public GameApiHandler(GameService gameService, GameMapper gameMapper, GameModeMapper gameModeMapper,
            GameStatusMapper gameStatusMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
        this.gameModeMapper = gameModeMapper;
        this.gameStatusMapper = gameStatusMapper;
    }

    @Override
    public ResponseEntity<GameWdto> createGame(GameCreateRequestWdto createGameRequest) {
        GameMode gameMode = gameModeMapper.fromWdto(createGameRequest.getGameMode());
        Game createdGame = gameService.createGame(createGameRequest.getPlayer1Id(),
                createGameRequest.getPlayer2Id(), gameMode);
        GameWdto gameWdto = gameMapper.toWdto(createdGame);

        URI location = ResourceLocationHelper.create(gameWdto.getId(), "gameId");
        return ResponseEntity.created(location).body(gameWdto);
    }

    @Override
    public ResponseEntity<GameWdto> getGameById(Long gameId) {
        Game game = gameService.getGameById(gameId);
        return ResponseEntity.ok(gameMapper.toWdto(game));
    }

    @Override
    public ResponseEntity<GameWdto> makeMove(Long gameId, MoveRequestWdto moveRequest) {
        Game updatedGame = gameService.makeMove(gameId, moveRequest.getColumn().byteValue());
        return ResponseEntity.ok(gameMapper.toWdto(updatedGame));
    }

    @Override
    public ResponseEntity<List<GameWdto>> getAllGames(
            GameStatusWdto gameStatusWdto,
            GameModeWdto gameModeWdto,
            Long playerId) {
        GameStatus status = (gameStatusWdto != null) ? gameStatusMapper.fromWdto(gameStatusWdto) : null;
        GameMode mode = (gameModeWdto != null) ? gameModeMapper.fromWdto(gameModeWdto) : null;

        List<Game> foundGames = gameService.findGames(status, mode, playerId);
        return ResponseEntity.ok(gameMapper.toWdtoList(foundGames));
    }

    @Override
    public ResponseEntity<Void> deleteGame(Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }
}
