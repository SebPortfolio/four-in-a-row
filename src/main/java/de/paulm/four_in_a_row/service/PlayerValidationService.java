package de.paulm.four_in_a_row.service;

import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.IllegalDisplayNameException;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.repository.PlayerProfileRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerValidationService {

    private final PlayerProfileRepository playerProfileRepository;

    public void validateDisplayName(String displayName, Long playerId) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalDisplayNameException(displayName, "null oder leer");
        }
        if (displayName.length() < PlayerProfile.DISPLAY_NAME_MIN_LENGTH) {
            throw new IllegalDisplayNameException(displayName,
                    "zu kurz, mindestens " + PlayerProfile.DISPLAY_NAME_MIN_LENGTH + " Zeichen");
        }
        if (!displayName.matches(PlayerProfile.DISPLAY_NAME_REGEX)) {
            throw new IllegalDisplayNameException(displayName, "ungültiges Format");
        }
        if (playerId == null && playerProfileRepository.existsByDisplayName(displayName)) {
            throw new IllegalDisplayNameException(displayName, "undefiniertes Problem");
        } else if (playerId != null && playerProfileRepository.existsByDisplayNameAndIdNot(displayName, playerId)) {
            throw new IllegalDisplayNameException(displayName, "undefiniertes Problem");
            // TODO: nur für Admins die klare Fehlerursache
            // es darf keine doppelten displayName geben, aber es soll nicht so transparent
            // sein, dass dieser displayName existiert und auch hier registriert ist
        }
    }

    public void validateUserId(Long userId) {
        if (userId == null) {
            // TODO: User beim Löschvorgang als ToBeDeleted markieren
            // und dann prüfen ob das für diesen User zutrifft, da nur für den Fall einer
            // User-Löschung die userId null werden darf
            throw new IllegalArgumentException(
                    "userId kann nicht null gesetzt werden, wenn der dazugehörige User nicht gelöscht werden soll");
            // TODO: richtige Exception erstellen
        }
    }

}
