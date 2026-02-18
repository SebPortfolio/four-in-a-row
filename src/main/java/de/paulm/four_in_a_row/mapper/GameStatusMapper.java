package de.paulm.four_in_a_row.mapper;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.game.GameStatus;
import de.paulm.model.GameStatusWdto;

@Mapper(componentModel = "spring")
public interface GameStatusMapper {

    GameStatusWdto toWdto(GameStatus gameStatus);

    GameStatus fromWdto(GameStatusWdto gameStatusWdto);

}
