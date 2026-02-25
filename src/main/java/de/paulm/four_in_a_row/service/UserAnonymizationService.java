package de.paulm.four_in_a_row.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnonymizationService {
    private final UserService userService;
    private final UserSessionService userSessionService;
    private final GameService gameService;
    private final PlayerProfileService profileService;

    @Transactional
    public void anonymizeFullUser(Long userId) {
        log.info("Anonymisierungs-Prozess gestartet für User: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("userId darf nicht null sein");
        }

        gameService.anonymizeGamesForUser(userId);
        profileService.anonymizeProfile(userId);
        userService.deleteUserById(userId);
        userSessionService.deleteAllSessionsByUserId(userId);

        log.info("Anonymisierungs-Prozess erfolgreich abgeschlossen.");
    }
}
