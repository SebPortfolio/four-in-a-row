package de.paulm.four_in_a_row.web.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.PlayerAdministrationApiDelegate;
import de.paulm.four_in_a_row.service.PlayerProfileService;
import de.paulm.model.PlayerPatchRequestWdto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlayerAdministrationApiHandler implements PlayerAdministrationApiDelegate {

    private final PlayerProfileService playerProfileService;

    @Override
    public ResponseEntity<Void> patchPlayerAsAdmin(Long playerId, PlayerPatchRequestWdto playerPatchRequestWdto) {
        playerProfileService.editDisplayName(playerId, playerPatchRequestWdto.getDisplayName());
        return ResponseEntity.noContent().build();
    }
}
