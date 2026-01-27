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
        profile.changeUsername(newName);
    }

    @Transactional
    public void editEmail(Long playerId, String newEmail) {
        PlayerProfile profile = this.getSpielerProfilById(playerId);
        profile.changeEmail(newEmail);
    }

    public PlayerStatistic getPlayerStatistic(Long playerId) {
        PlayerProfile profile = this.getSpielerProfilById(playerId);
        return profile.getStatistic();
    }
}
