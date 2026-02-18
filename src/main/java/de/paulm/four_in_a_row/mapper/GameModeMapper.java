package de.paulm.four_in_a_row.mapper;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.game.GameMode;
import de.paulm.model.GameModeWdto;

@Mapper(componentModel = "spring")
public interface GameModeMapper {

    GameModeWdto toWdto(GameMode gameMode);

    GameMode fromWdto(GameModeWdto gameModeWdto);

}
