package de.paulm.four_in_a_row.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.PlayerApiDelegate;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.domain.player.PlayerStatistic;
import de.paulm.four_in_a_row.mapper.PlayerMapper;
import de.paulm.four_in_a_row.service.PlayerProfileService;
import de.paulm.four_in_a_row.service.PlayerStatisticService;
import de.paulm.model.PlayerWdto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlayerApiHandler implements PlayerApiDelegate {

    private final PlayerProfileService playerProfileService;
    private final PlayerStatisticService playerStatisticService;
    private final PlayerMapper playerMapper;

    @Override
    public ResponseEntity<List<PlayerWdto>> getAllPlayers(String term, Integer limit) {
        List<PlayerProfile> profiles = playerProfileService.findProfiles(term, limit);

        return ResponseEntity.ok(playerMapper.toWdtoList(profiles));
    }

    @Override
    public ResponseEntity<PlayerWdto> getPlayerById(Long playerId) { // FIXME: warum doppelte Repo Aufrufe?!
        PlayerProfile profile = playerProfileService.getProfileById(playerId);
        PlayerStatistic statistic = playerStatisticService.getStatisticById(playerId);
        profile.setStatistic(statistic);
        PlayerWdto playerDto = playerMapper.toWdto(profile);
        return ResponseEntity.ok(playerDto);
    }

    @Override
    public ResponseEntity<PlayerWdto> updatePlayer(Long playerId, PlayerWdto playerWdto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        // TODO: Implementieren: Spielerprofil aktualisieren
    }
}
