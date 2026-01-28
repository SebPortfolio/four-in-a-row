package de.paulm.four_in_a_row.web.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.paulm.api.GameApiDelegate;
import de.paulm.four_in_a_row.game.Game;
import de.paulm.four_in_a_row.mapper.GameMapper;
import de.paulm.four_in_a_row.service.GameService;
import de.paulm.model.GameCreateRequestDto;
import de.paulm.model.GameDto;

@Service
public class GameApiHandler implements GameApiDelegate {

    private final GameService gameService;
    private final GameMapper gameMapper;

    public GameApiHandler(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    @Override
    public ResponseEntity<GameDto> createGame(GameCreateRequestDto createGameRequest) {
        Game createdGame = gameService.createGame(createGameRequest.getPlayer1Id(),
                createGameRequest.getPlayer2Id());
        GameDto gameDto = gameMapper.toDto(createdGame);
        return ResponseEntity.ok(gameDto);
    }
}
