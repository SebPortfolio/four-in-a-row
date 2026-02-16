package de.paulm.four_in_a_row.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.IllegalEmailException;
import de.paulm.four_in_a_row.domain.exceptions.IllegalUsernameException;
import de.paulm.four_in_a_row.domain.exceptions.PlayerProfileNotFoundException;
import de.paulm.four_in_a_row.player.PlayerProfile;
import de.paulm.four_in_a_row.player.PlayerStatistic;
import de.paulm.four_in_a_row.repository.PlayerProfileRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {

    private final PlayerProfileRepository repository;
    private final PlayerStatisticService statisticService;

    // TODO: limit implementieren
    public List<PlayerProfile> findPlayers(String term, Integer limit) {
        if (term == null || term.isBlank()) {
            return repository.findAll();
        }

        // TODO: seperaten Endpunkt als getPlayerForBackendSelect

        return repository.findByUsernameContainingIgnoreCase(term);
    }

    public List<PlayerProfile> getAllPlayerProfiles() {
        return repository.findAll();
    }

    public List<PlayerProfile> getAllPlayerProfilesWithStatistic() {
        return repository.findAllWithStatistic();
    }

    public boolean doesPlayerProfileExsistById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }

        return repository.existsById(id);
    }

    @NonNull
    @SuppressWarnings("null")
    public PlayerProfile getPlayerProfileById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }
        return repository.findById(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id));
    }

    @NonNull
    @SuppressWarnings("null")
    public PlayerProfile getPlayerProfileByIdWithStatistic(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }
        return repository.findByIdWithStatistic(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id));
    }

    @Transactional
    public void editUsername(Long playerId, String newName) {
        PlayerProfile profile = this.getPlayerProfileById(playerId);
        if (newName == null || newName.isBlank()) {
            throw new IllegalUsernameException(newName, "Benutzername darf nicht leer sein");
        }
        if (newName.length() < 3) {
            throw new IllegalUsernameException(newName, "Benutzername muss mindestens 3 Zeichen haben");
        }
        profile.setUsername(newName);
    }

    @Transactional
    public void editEmail(Long playerId, String newEmail) {
        PlayerProfile profile = this.getPlayerProfileById(playerId);
        if (newEmail == null || !newEmail.matches(".+@.+\\..+")) {
            throw new IllegalEmailException(newEmail, "ungültiges Format");
        }
        profile.setEmail(newEmail);
    }

    @Transactional
    public PlayerProfile createPlayerProfile(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalUsernameException(username, "Benutzername darf nicht leer sein");
        }
        if (username.length() < 3) {
            throw new IllegalUsernameException(username, "Benutzername muss mindestens 3 Zeichen haben");
        }
        PlayerProfile profile = buildProfile(username, email);
        PlayerStatistic statistic = statisticService.createStatistic(profile);
        profile.setStatistic(statistic);

        return repository.save(profile);
    }

    private PlayerProfile buildProfile(String username, String email) {
        PlayerProfile profile = new PlayerProfile();
        profile.setUsername(username);
        profile.setEmail(email);
        profile.setRegisteredOn(LocalDate.now());
        return profile;
    }

    @Transactional
    public void deletePlayerProfile(Long playerId) {
        PlayerProfile profile = this.getPlayerProfileById(playerId);
        repository.delete(profile);
    }
}
