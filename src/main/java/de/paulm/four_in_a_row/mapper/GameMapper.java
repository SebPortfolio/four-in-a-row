package de.paulm.four_in_a_row.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import de.paulm.four_in_a_row.game.Game;
import de.paulm.model.GameWdto;

@Mapper(componentModel = "spring", uses = { PlayerMapper.class })
public interface GameMapper {

    @Mapping(source = "currentPlayer.id", target = "currentPlayerId")
    GameWdto toWdto(Game game);

    @Mapping(target = "currentPlayer", ignore = true)
    Game toEntity(GameWdto gameDto);

    List<GameWdto> toWdtoList(List<Game> games);

    List<Game> toEntityList(List<GameWdto> gameDtos);

    default byte[][] mapBoardFromList(List<List<Integer>> value) {
        if (value == null) {
            return null;
        }

        int rows = value.size();
        int cols = value.isEmpty() ? 0 : value.get(0).size();
        byte[][] board = new byte[rows][cols];

        for (int i = 0; i < rows; i++) {
            List<Integer> row = value.get(i);
            for (int j = 0; j < row.size(); j++) {
                Integer cell = row.get(j);
                board[i][j] = (cell != null) ? cell.byteValue() : 0;
            }
        }
        return board;
    }

    default List<List<Integer>> mapBoardtoList(byte[][] value) {
        if (value == null)
            return null;

        List<List<Integer>> list = new ArrayList<>();
        for (byte[] row : value) {
            List<Integer> rowList = new ArrayList<>();
            for (byte b : row) {
                rowList.add((int) b);
            }
            list.add(rowList);
        }
        return list;
    }

    @AfterMapping
    default void linkCurrentPlayer(GameWdto wdto, @MappingTarget Game entity) {
        if (wdto == null || wdto.getCurrentPlayerId() == null) {
            return;
        }

        Long currentPlayerId = wdto.getCurrentPlayerId();
        if (wdto.getPlayer1().getId().equals(currentPlayerId)) {
            entity.setCurrentPlayer(entity.getPlayer1());
        } else if (wdto.getPlayer2().getId().equals(currentPlayerId)) {
            entity.setCurrentPlayer(entity.getPlayer2());
        } else {
            throw new IllegalArgumentException("Current player ID" + currentPlayerId +
                    " does not match either player in the game");
        }
    }
}
