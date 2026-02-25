package de.paulm.four_in_a_row.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
            throw new IllegalArgumentException("id darf nicht null sein");
        }

        return repository.existsById(id);
    }

    @NonNull
    public PlayerProfile getProfileById(Long id) throws PlayerProfileNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }
        return Objects.requireNonNull(
                repository.findById(id)
                        .orElseThrow(() -> new PlayerProfileNotFoundException(id, "id")));
    }

    @NonNull
    public PlayerProfile getProfileByIdWithStatistic(Long id)
            throws PlayerProfileNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }
        return Objects.requireNonNull(repository.findByIdWithStatistic(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id, "id")));
    }

    @Transactional
    public void editDisplayName(PlayerProfile profile, String newName) throws IllegalArgumentException {
        validateProfile(newName);
        profile.setDisplayName(newName);
    }

    @Transactional
    public PlayerProfile createProfileForUser(Long userId, String displayName) {
        PlayerProfile profile = this.buildInitalProfile(userId, displayName);
        PlayerStatistic initialStats = statisticService.buildInitialStatistic(profile);
        profile.setStatistic(initialStats);

        return repository.save(profile);
    }

    @NonNull
    private PlayerProfile buildInitalProfile(Long userId, String displayName) {
        return Objects.requireNonNull(PlayerProfile.builder()
                .userId(userId)
                .displayName(displayName)
                .registeredOn(LocalDate.now())
                .build());
    }

    @NonNull
    public PlayerProfile getProfileByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId darf nicht null sein");
        }
        return Objects.requireNonNull(repository.findByUserId(userId)
                .orElseThrow(() -> new PlayerProfileNotFoundException(userId, "userId")));
    }

    private void validateProfile(String displayName) throws IllegalDisplayNameException {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalDisplayNameException(displayName, "leer");
        }
        if (displayName.length() < PlayerProfile.DISPLAY_NAME_MIN_LENGTH) {
            throw new IllegalDisplayNameException(displayName,
                    "muss mindestens " + PlayerProfile.DISPLAY_NAME_MIN_LENGTH + " Zeichen haben");
        }
        if (!displayName.matches(PlayerProfile.DISPLAY_NAME_REGEX)) {
            throw new IllegalDisplayNameException(displayName, "ungültiges Format");
        }
    }
}
