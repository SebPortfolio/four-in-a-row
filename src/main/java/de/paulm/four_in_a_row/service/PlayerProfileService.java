package de.paulm.four_in_a_row.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.IllegalDisplayNameException;
import de.paulm.four_in_a_row.domain.exceptions.PlayerProfileNotFoundException;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.domain.player.PlayerStatistic;
import de.paulm.four_in_a_row.repository.PlayerProfileRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {

    private final PlayerProfileRepository repository;
    private final PlayerStatisticService statisticService;

    // TODO: limit implementieren
    public List<PlayerProfile> findProfiles(String term, Integer limit) {
        if (term == null || term.isBlank()) {
            return repository.findAll();
        }

        // TODO: seperaten Endpunkt als getPlayerForBackendSelect ?!

        return repository.findByDisplayNameContainingIgnoreCase(term);
    }

    public List<PlayerProfile> getAllPlayerProfiles() {
        return repository.findAll();
    }

    public List<PlayerProfile> getAllProfilesWithStatistic() {
        return repository.findAllWithStatistic();
    }

    public boolean doesProfileExsistById(Long id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }

        return repository.existsById(id);
    }

    @NonNull
    @SuppressWarnings("null")
    public PlayerProfile getProfileById(Long id) throws PlayerProfileNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }
        return repository.findById(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id, "id"));
    }

    @NonNull
    @SuppressWarnings("null")
    public PlayerProfile getProfileByIdWithStatistic(Long id)
            throws PlayerProfileNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }
        return repository.findByIdWithStatistic(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id, "id"));
    }

    @Transactional
    public void editDisplayName(Long playerId, String newName) throws IllegalArgumentException {
        PlayerProfile profile = this.getProfileById(playerId);
        if (newName == null || newName.isBlank()) {
            throw new IllegalDisplayNameException(newName, "Anzeigename darf nicht leer sein");
        }
        if (newName.length() < 3) {
            throw new IllegalDisplayNameException(newName, "Anzeigename muss mindestens 3 Zeichen haben");
        }
        profile.setDisplayName(newName);
    }

    @Transactional
    public PlayerProfile createProfileForUser(Long userId, String displayName) {
        PlayerProfile profile = this.buildInitalProfile(userId, displayName);
        PlayerStatistic initialStats = statisticService.buildInitialStatistic(profile);
        profile.setStatistic(initialStats);

        return repository.save(profile);
    }

    private PlayerProfile buildInitalProfile(Long userId, String displayName) {
        return PlayerProfile.builder()
                .userId(userId)
                .displayName(displayName)
                .registeredOn(LocalDate.now())
                .build();
    }

    public PlayerProfile getProfileByUserId(Long userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new PlayerProfileNotFoundException(userId, "userId"));
    }
}
