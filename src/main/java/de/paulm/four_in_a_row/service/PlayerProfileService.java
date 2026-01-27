package de.paulm.four_in_a_row.service;

import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.PlayerProfileNotFoundException;
import de.paulm.four_in_a_row.profil.PlayerProfile;
import de.paulm.four_in_a_row.profil.PlayerStatistic;
import de.paulm.four_in_a_row.repository.PlayerProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {

    private final PlayerProfileRepository repository;

    public PlayerProfile getSpielerProfilById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id));
    }

    @Transactional
    public void editUsername(Long playerId, String newName) {
        PlayerProfile profile = this.getSpielerProfilById(playerId);
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Benutzername darf nicht leer sein");
        }
        if (newName.length() < 3) {
            throw new IllegalArgumentException("Benutzername muss mindestens 3 Zeichen haben");
        }
        profile.setUsername(newName);
    }

    @Transactional
    public void editEmail(Long playerId, String newEmail) {
        PlayerProfile profile = this.getSpielerProfilById(playerId);
        if (newEmail == null || !newEmail.matches(".+@.+\\..+")) {
            throw new IllegalArgumentException("Ungültige Email");
        }
        profile.setEmail(newEmail);
    }

    public PlayerStatistic getPlayerStatistic(Long playerId) {
        PlayerProfile profile = this.getSpielerProfilById(playerId);
        return profile.getStatistic();
    }
}
