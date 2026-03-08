package de.paulm.four_in_a_row.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PlayerValidationService validationService;

    // TODO: limit implementieren
    public List<PlayerProfile> findProfilesByDisplayNameTerm(String term, Integer limit) {
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

    public boolean doesProfileExsistById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }

        return repository.existsById(id);
    }

    @NonNull
    public PlayerProfile getProfileById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }
        return Objects.requireNonNull(
                repository.findById(id)
                        .orElseThrow(() -> new PlayerProfileNotFoundException(id, "id")));
    }

    @NonNull
    public PlayerProfile getProfileByIdWithStatistic(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }
        return Objects.requireNonNull(repository.findByIdWithStatistic(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id, "id")));
    }

    @Transactional
    public PlayerProfile editDisplayName(Long playerId, String newDisplayName) {
        PlayerProfile player = getProfileById(playerId);
        if (newDisplayName != null && !newDisplayName.equals(player.getDisplayName())) {
            applyDisplayNameChange(player, newDisplayName);
        }
        return player;
    }

    private void applyDisplayNameChange(PlayerProfile profile, String newDisplayName) {
        validationService.validateDisplayName(newDisplayName, profile.getId());
        profile.setDisplayName(newDisplayName);
    }

    @Transactional
    public PlayerProfile createPlayerWithProfileAndStatistic(Long userId, String displayName) {
        if (userId == null) {
            throw new IllegalArgumentException("userId darf nicht null sein");
        }
        validationService.validateDisplayName(displayName, null);
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

    @Transactional
    public void anonymizeProfile(@NonNull Long userId) {
        PlayerProfile profile = getProfileByUserId(userId);

        profile.setUserId(null);
        applyDisplayNameChange(profile, "DeletedPlayer_" + profile.getId());
    }
}
