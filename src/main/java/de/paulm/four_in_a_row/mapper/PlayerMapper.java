package de.paulm.four_in_a_row.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.player.PlayerProfile;
import de.paulm.model.PlayerWdto;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mapping(source = "statistic.totalGames", target = "totalGames")
    @Mapping(source = "statistic.gamesWon", target = "gamesWon")
    @Mapping(source = "statistic.gamesLost", target = "gamesLost")
    @Mapping(source = "statistic.gamesSurrendered", target = "gamesSurrendered")
    @Mapping(source = "statistic.lastPlayedOn", target = "lastPlayedOn")
    PlayerWdto toWdto(PlayerProfile playerProfile);

    @Mapping(ignore = true, target = "email")
    @Mapping(source = "totalGames", target = "statistic.totalGames")
    @Mapping(source = "gamesWon", target = "statistic.gamesWon")
    @Mapping(source = "gamesLost", target = "statistic.gamesLost")
    @Mapping(source = "gamesSurrendered", target = "statistic.gamesSurrendered")
    @Mapping(source = "lastPlayedOn", target = "statistic.lastPlayedOn")
    PlayerProfile toProfileEntity(PlayerWdto playerProfileDto);

    List<PlayerWdto> toWdtoList(List<PlayerProfile> playerProfiles);

    List<PlayerProfile> toEntityList(List<PlayerWdto> playerProfileDtos);
}
