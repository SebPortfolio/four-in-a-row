package de.paulm.four_in_a_row.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.PlayerApiDelegate;
import de.paulm.four_in_a_row.mapper.PlayerMapper;
import de.paulm.four_in_a_row.player.PlayerProfile;
import de.paulm.four_in_a_row.service.PlayerProfileService;
import de.paulm.four_in_a_row.service.PlayerStatisticService;
import de.paulm.model.CreatePlayerRequestWdto;
import de.paulm.model.PlayerWdto;

@RestController
public class PlayerApiHandler implements PlayerApiDelegate {

    private final PlayerProfileService playerProfileService;
    private final PlayerStatisticService playerStatisticService;
    private final PlayerMapper playerMapper;

    public PlayerApiHandler(PlayerProfileService playerProfileService, PlayerStatisticService playerStatisticService,
            PlayerMapper playerMapper) {
        this.playerProfileService = playerProfileService;
        this.playerStatisticService = playerStatisticService;
        this.playerMapper = playerMapper;
    }

    @Override
    public ResponseEntity<List<PlayerWdto>> getAllPlayers() {
        List<PlayerProfile> profiles = playerProfileService.getAllPlayerProfilesWithStatistic();
        List<PlayerWdto> playerDtos = playerMapper.toWdtoList(profiles);
        return ResponseEntity.ok(playerDtos);
    }

    @Override
    public ResponseEntity<PlayerWdto> getPlayerById(Long playerId) {
        var profile = playerProfileService.getPlayerProfileById(playerId);
        var statistic = playerStatisticService.getStatisticById(playerId);
        profile.setStatistic(statistic);
        PlayerWdto playerDto = playerMapper.toWdto(profile);
        return ResponseEntity.ok(playerDto);
    }

    @Override
    public ResponseEntity<PlayerWdto> createPlayer(CreatePlayerRequestWdto createPlayerRequestWdto) {
        PlayerProfile createdProfile = playerProfileService.createPlayerProfile(createPlayerRequestWdto.getUsername(),
                createPlayerRequestWdto.getEmail());
        PlayerWdto playerWdto = playerMapper.toWdto(createdProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(playerWdto);
    }

    @Override
    public ResponseEntity<PlayerWdto> updatePlayer(Long playerId, PlayerWdto playerWdto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        // TODO: Implementieren: Spielerprofil aktualisieren
    }

    @Override
    public ResponseEntity<Void> deletePlayer(Long playerId) {
        playerProfileService.deletePlayerProfile(playerId);
        return ResponseEntity.noContent().build();
    }
}
