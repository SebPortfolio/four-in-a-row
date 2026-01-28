package de.paulm.four_in_a_row.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.game.Game;
import de.paulm.model.GameDto;

@Mapper(componentModel = "spring", uses = { PlayerMapper.class })
public interface GameMapper {

    GameDto toDto(Game game);

    Game toEntity(GameDto gameDto);

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
}
